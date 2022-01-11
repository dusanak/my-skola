/*
 * SSL client for project in PVOS.
 *
 * petr.olivka@vsb.cz, Dec. 07, 2020
 * Dept. of Computer Science, FEECS, VSB-TUO
 */

/* cli.cpp	-  Minimal ssleay client for Unix
   30.9.1996, Sampo Kellomaki <sampo@iki.fi> */

/* mangled to work with SSLeay-0.9.0b and OpenSSL 0.9.2b
   Simplified to be even more minimal
   12/98 - 4/99 Wade Scholine <wades@mail.cybg.com> */

#include <stdio.h>
#include <unistd.h>
#include <memory.h>
#include <string.h>
#include <errno.h>
#include <sys/param.h>
#include <wait.h>
#include <semaphore.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

#include <queue>

#include <openssl/crypto.h>
#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/ssl.h>
#include <openssl/err.h>


#define CHK_NULL(x) if ((x)==NULL) exit (1)
#define CHK_ERR(err,s) if ((err)==-1) { perror(s); exit(1); }
#define CHK_SSL(err) if ((err)==-1) { ERR_print_errors_fp(stderr); exit(2); }
#define eprintf( args... ) fprintf( stderr, ## args )

#define MIN_LINE_LEN		64
#define MAX_LINE_LEN		512
#define DEFAULT_LINE_LEN	128
#define MAX_FORK			1000
#define DEFAULT_SERVER		"localhost"
#define DEFAULT_PORT		"1111"
#define QUEUE_LIMIT			1000

// records of generated lines for verification
struct line_info
{
	long line, sum, total;
};

std::queue<line_info> queue_line_info;

// queue control
sem_t sem_queue_mutex, sem_queue_limit;

// ssl thread for answers is running
int ssl_thread_running = 0;

int ssl_readline( SSL *ssl, char *buf, int length )
{
	int num = 0;
	while ( num < length - 1 )
	{
		int err = SSL_read( ssl, buf + num, 1 );
		if ( err <= 0 ) return err;
		if ( buf[ num++ ] == '\n' ) break;
	}
	buf[ num ] = 0;
	return num;
}

// thread for answers
void *ssl_thread( void * thread_arg )
{
	SSL *ssl = ( SSL * ) thread_arg;
	char buf[ 4096 ];
	while ( 1 )
	{
		int err;
		err = ssl_readline (ssl, buf, sizeof(buf) - 1);					CHK_SSL(err);

		line_info qli = { -1, 0, 0 }, rli;
		if ( 3 != sscanf( buf, "%ld %ld %ld", &rli.line, &rli.sum, &rli.total ) )
		{
			eprintf( "Unable to parse received line: '%s'", buf );
			break;
		}
		if ( !queue_line_info.size() )
		{
			eprintf( "Queue_line_info is empty!\n" );
			break;
		}

		err = sem_wait( &sem_queue_mutex );								CHK_ERR(err, "wait sem_queue_mutex");
		qli = queue_line_info.front();
		queue_line_info.pop();
		sem_post( &sem_queue_mutex );									CHK_ERR(err, "post sem_queue_mutex");
		sem_post( &sem_queue_limit );									CHK_ERR(err, "post sem_queue_limit");

		if ( rli.line != qli.line || rli.sum != qli.sum || rli.total != qli.total )
		{
			eprintf( "Current queue_line_info does not match the received line: line:%ld?%ld sum:%ld?%ld total:%ld?%ld\n",
					rli.line, qli.line, rli.sum, qli.sum, rli.total, qli.total );
			break;
		}

		int sem_limit;
		sem_getvalue( &sem_queue_limit, &sem_limit );
		if ( !( rli.line % 1000 ) )
			eprintf( "Received back %ld lines (total %ld bytes, sem_limit %d)\n", rli.line, rli.total, sem_limit );
	}
	ssl_thread_running = 0;
	return NULL;
}

// single socket/ssl client
void ssl_fork( sockaddr_in *addr, int avg_line_len )
{
	int err;
	int sd;
	SSL_CTX* ctx;
	SSL*	 ssl;
	const SSL_METHOD *meth;

	SSLeay_add_ssl_algorithms();
	meth = SSLv23_client_method();
	SSL_load_error_strings();
	ctx = SSL_CTX_new (meth);									CHK_NULL(ctx);

	/* ----------------------------------------------- */
	/* Create a socket and connect to server using normal socket calls. */

	sd = socket (AF_INET, SOCK_STREAM, 0);						CHK_ERR(sd, "socket");

	err = connect(sd, ( sockaddr * ) addr,	sizeof(*addr));		CHK_ERR(err, "connect");

	/* ----------------------------------------------- */
	/* Now we have TCP connection. Start SSL negotiation. */

	ssl = SSL_new (ctx);										CHK_NULL(ssl);
	SSL_set_fd (ssl, sd);
	err = SSL_connect (ssl);									CHK_SSL(err);

	/* Following two steps are optional and not required for
	 data exchange to be successful. */

	/* Get the cipher - opt */

	printf ("SSL connection using %s\n", SSL_get_cipher (ssl));

	// semaphores for queue
	err = sem_init( &sem_queue_mutex, 0, 1 );					CHK_ERR(err, "init sem_queue_mutex");
	err = sem_init( &sem_queue_limit, 0, QUEUE_LIMIT );			CHK_ERR(err, "init sem_queue_limit");

	srand( getpid() );

	char buf [16384] = "";
	char line[ avg_line_len * 2 ] = "";
	long total_bytes = 0;
	long num_lines = 0;

	const char *test_req = "test";
	const char *test_ack = "ready";

	err = SSL_write( ssl, test_req, strlen( test_req ) );		CHK_ERR( err, "Unable to send test req" );
	err = SSL_read( ssl, buf, sizeof( buf ) );					CHK_ERR( err, "Unable get test ack" );

	if ( strncmp( buf, test_ack, strlen( test_ack ) ) )
	{
		eprintf( "Bad test ack" );
		ssl_thread_running = 0;
	}
	else
	{
		printf( "Press Enter to start test.\n" );
		getchar();
	}

	buf[ 0 ] = 0;

	// thread for answers
	pthread_t ssl_tid = 0;
	if ( !pthread_create( &ssl_tid, NULL, ssl_thread, ssl ) )
	{
		ssl_thread_running = 1;
		printf( "Thread for answers created.\n" );
	}
	else
	{
		eprintf( "Unable to create thread for answers!\n");
	}

	// line generator
	while ( ssl_thread_running )
	{
		// generate a few lines into buf
		while ( sizeof( buf ) - strlen( buf ) > strlen( line ) )
		{
			strcat( buf, line );
			int limit_len = avg_line_len / 2 + rand() % avg_line_len - 10;
			int line_len = sprintf( line, "(%d)", getpid() );
			int sum = 0;
			while ( line_len < limit_len )
			{
				int tmp = rand() % 100000000;
				sum += tmp;
				line_len += sprintf( line + line_len, " %d", tmp );
			}

			line_len += sprintf( line + line_len, " %d\n", sum );
			total_bytes += line_len;

			line_info li = { num_lines++, sum, total_bytes };

			err = sem_wait( &sem_queue_limit );					CHK_ERR(err, "wait sem_queue_limit");
			err = sem_wait( &sem_queue_mutex );					CHK_ERR(err, "wait sem_queue_mutex");
			queue_line_info.push( li );
			err = sem_post( &sem_queue_mutex );					CHK_ERR(err, "post sem_queue_mutex");
		}
		// send lines from buf
		int buflen = strlen( buf );
		int inx = 0;
		while ( 1 )
		{
			int len = 400 + rand() % 1000;
			if ( buflen - inx < len ) break;
			int err = SSL_write( ssl, buf + inx, len );			CHK_SSL(err);
			inx += err;
		}
		// remove sent data
		strcpy( buf, buf + inx );
	}

	if ( ssl_tid ) pthread_join( ssl_tid, NULL );

	SSL_shutdown (ssl);  /* send SSL/TLS close_notify */

	/* Clean up. */

	close (sd);
	SSL_free (ssl);
	SSL_CTX_free (ctx);
}

int main ( int argn, char ** args )
{
	const char *str_addr = NULL;
	const char *str_port = NULL;
	int num_fork = 1;
	int avg_line_len = DEFAULT_LINE_LEN;

	// help?
	for ( int i = 1; i < argn; i++ )
		if ( !strcmp( args[ i ], "-h" ) )
		{
			printf(
					"Usage: %s [-h] [-f num] [-l len] [server_ip_or_name] [port_num]\n"
					"\n"
					"  -h this help\n"
					"  -f number of children (max %d)\n"
					"  -l average line length (min/max/default %d/%d/%d)\n"
					"  default server: %s\n"
					"  default port: %s\n"
					"\n",
					args[ 0 ], MAX_FORK, MIN_LINE_LEN, MAX_LINE_LEN, DEFAULT_LINE_LEN, DEFAULT_SERVER, DEFAULT_PORT
					);
			exit( 0 );
		}

	// parse arguments
	for ( int i = 1; i < argn; i++ )
	{
		if ( !strcmp( args[ i ], "-f" ) )
		{
			if ( ++i >= argn ) break;
			num_fork = atoi( args[ i ] );
			num_fork = MAX( MIN( num_fork, MAX_FORK ), 1 );

		}
		else if ( !strcmp( args[ i ], "-l" ) )
		{
			if ( ++i >= argn ) break;
			avg_line_len = atoi( args[ i ] );
			avg_line_len = MAX( MIN( avg_line_len, MAX_LINE_LEN ), MIN_LINE_LEN );
		}
		else if ( *args[ i ] != '-' )
		{
			if ( !str_addr ) str_addr = args[ i ];
			else if ( !str_port ) str_port = args[ i ];
			else eprintf( "Unused parameter %s.", args[ i ] );
		}
		else
			eprintf( "Unknown option %s\n", args[ i ] );
	}

	if ( !str_addr ) str_addr = DEFAULT_SERVER;
	if ( !str_port ) str_port = DEFAULT_PORT;

	struct sockaddr_in sa, *psa;

	// translate DNS name to IP address
	addrinfo addr_hints = {
		  .ai_flags = 0,
		  .ai_family = AF_INET,
		  .ai_socktype = SOCK_STREAM,
		  .ai_protocol = 0 };
	addrinfo *addr_results;

	int ret_gai = getaddrinfo( str_addr, str_port, &addr_hints, &addr_results );

	if ( ret_gai ) { eprintf( "getaddrinfo: %s\n", gai_strerror( ret_gai ) ); exit( 1 ); }

	if ( sizeof( sa ) != addr_results->ai_addrlen ) { eprintf( "Bad INET address size!\n" ); exit( 1 ); }

	memcpy( &sa, addr_results->ai_addr, sizeof( sa ) );

	freeaddrinfo( addr_results );

	// create children
	for ( int i = 0; i < num_fork; i++ )
	{
		int pid = fork();										CHK_ERR( pid, "Unable to create child process!\n" );
		if ( !pid )
		{
		  ssl_fork( &sa, avg_line_len );
		  exit( 0 );
		}
	}

	// wait for children
	int ret_wait;
	while ( ( ret_wait = wait( NULL ) ) != -1 )
		printf( "Child %d finished.\n", ret_wait );

}
/* EOF - ssl_cli.cpp */
