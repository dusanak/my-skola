/*
 * SSL server for project in PVOS.
 *
 * petr.olivka@vsb.cz, Oct. 27, 2018
 * Dept. of Computer Science, FEECS, VSB-TUO
 */


/* serv.cpp  -	Minimal ssleay server for Unix
   30.9.1996, Sampo Kellomaki <sampo@iki.fi> */


/* mangled to work with SSLeay-0.9.0b and OpenSSL 0.9.2b
   Simplified to be even more minimal
   12/98 - 4/99 Wade Scholine <wades@mail.cybg.com> */

#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <memory.h>
#include <thread>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

#include <vector>
#include <signal.h>

#include <openssl/rsa.h>	   /* SSLeay stuff */
#include <openssl/crypto.h>
#include <openssl/x509.h>
#include <openssl/pem.h>
#include <openssl/ssl.h>
#include <openssl/err.h>

/* define HOME to be dir for key and cert files... */
#define HOME "./"
/* Make these what you want for cert & key files */
#define CERTF  HOME "myserver.crt"
#define KEYF  HOME	"mypriv.pem"

#define CHK_NULL(x) if ((x)==NULL) { printf("CHK_NULL\n"); exit (1); }
#define CHK_ERR(err,s) if ((err)==-1) { printf("CHK_ERR\n"); perror(s); exit(1); }
#define eprintf( args... ) fprintf( stderr, ## args )

bool accept_connections = true;

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

void *ssl_thread( void * thread_arg ) {
	SSL* ssl = ( SSL * ) thread_arg;
	int err;

	// /* Get the cipher - opt */

	// printf ("SSL connection using %s\n", SSL_get_cipher (ssl));

	// /* Get client's certificate (note: beware of dynamic allocation) - opt */

	// X509* client_cert = SSL_get_peer_certificate (ssl);
	// if (client_cert != NULL) {
	// 	printf ("Client certificate:\n");

	// 	char *str = X509_NAME_oneline (X509_get_subject_name (client_cert), 0, 0);
	// 	CHK_NULL(str);
	// 	printf ("\t subject: %s\n", str);
	// 	OPENSSL_free (str);

	// 	str = X509_NAME_oneline (X509_get_issuer_name  (client_cert), 0, 0);
	// 	CHK_NULL(str);
	// 	printf ("\t issuer: %s\n", str);
	// 	OPENSSL_free (str);

	// 	/* We could do all sorts of certificate verification stuff here before
	// 	deallocating the certificate. */

	// 	X509_free (client_cert);
	// } else
	// 	printf ("Client does not have certificate.\n");

	const char *test_req = "test";
	const char *test_ack = "ready";
	int run_test = 1;
	char buf [4096];

	err = SSL_read( ssl, buf, sizeof( buf ) );					CHK_ERR( err, "Unable get test req" );

	if ( strncmp( buf, test_req, strlen( test_req ) ) )
	{
		eprintf( "Bad test req." );
		run_test = 0;
	}
	else
	{
		err = SSL_write( ssl, test_ack, strlen( test_ack ) );		CHK_ERR( err, "Unable to send test ack" );
		printf( "Sent test ack.\n" );
	}

	/* DATA EXCHANGE - Receive message and send reply. */

	long total_bytes = 0;
	long num_lines = 0;
	while ( run_test )
	{
		// read one line
		err = ssl_readline (ssl, buf, sizeof(buf) - 1);					
		if ( err <= 0 ) {
			ERR_print_errors_fp(stderr);
			printf("Breaking! %d\n", gettid());
			break;
		}

		total_bytes += err;

		// parse line
		int tmp, parsed, buf_inx;
		long sum = 0;
		sscanf( buf, "(%d)%n", &tmp, &buf_inx );
		while ( 1 == sscanf( buf + buf_inx, "%d%n", &tmp, &parsed  ) )
		{
			sum += tmp;
			buf_inx += parsed;
		}
		sum /= 2;

		// send answer
		sprintf( buf, "%ld %ld %ld\n", num_lines++, sum, total_bytes );
		err = SSL_write( ssl, buf, strlen( buf ) );
		if ( err <= 0 ) {
			ERR_print_errors_fp(stderr);
			printf("Breaking! %d\n", gettid());
			break;
		}			
	}

	/* Clean up. */

	// close (SSL_get_fd(ssl));
	// SSL_free (ssl);

	printf("Thread %d finished!\n", gettid());
	return nullptr;
}

int main ()
{
	int err;
	int listen_sd;
	struct sockaddr_in sa_serv;
	struct sockaddr_in sa_cli;
	socklen_t client_len;
	SSL_CTX* ctx;
	X509*	 client_cert;
	const SSL_METHOD *meth;

	std::vector<pthread_t> threads;

	// Block SIGPIPE so closing of ssl client doesn't crash program.
	sigset_t set;
	sigemptyset(&set);
	sigaddset(&set, SIGPIPE);
	if (pthread_sigmask(SIG_BLOCK, &set, NULL) != 0)
		return -1;

	/* SSL preliminaries. We keep the certificate and key with the context. */

	SSL_load_error_strings();
	SSLeay_add_ssl_algorithms();
	meth = SSLv23_server_method();
	ctx = SSL_CTX_new (meth);
	if (!ctx) {
		ERR_print_errors_fp(stderr);
		exit(2);
	}

	if (SSL_CTX_use_certificate_file(ctx, CERTF, SSL_FILETYPE_PEM) <= 0) {
		ERR_print_errors_fp(stderr);
		exit(3);
	}
	if (SSL_CTX_use_PrivateKey_file(ctx, KEYF, SSL_FILETYPE_PEM) <= 0) {
		ERR_print_errors_fp(stderr);
		exit(4);
	}

	if (!SSL_CTX_check_private_key(ctx)) {
		fprintf(stderr,"Private key does not match the certificate public key\n");
		exit(5);
	}

	/* ----------------------------------------------- */
	/* Prepare TCP socket for receiving connections */

	listen_sd = socket (AF_INET, SOCK_STREAM, 0);	CHK_ERR(listen_sd, "socket");

	memset (&sa_serv, '\0', sizeof(sa_serv));
	sa_serv.sin_family		= AF_INET;
	sa_serv.sin_addr.s_addr = INADDR_ANY;
	sa_serv.sin_port		= htons (1111);			 /* Server Port number */

	err = bind(listen_sd, (struct sockaddr*) &sa_serv,
	 sizeof (sa_serv));					  CHK_ERR(err, "bind");

	while (accept_connections) {
		/* Receive a TCP connection. */

		err = listen (listen_sd, 1);					CHK_ERR(err, "listen");
		if ((err)==-1) { printf("CHK_ERR\n"); perror("listen"); exit(1); }
		printf("Listening\n");

		client_len = sizeof(sa_cli);
		int sd = accept (listen_sd, (struct sockaddr*) &sa_cli, &client_len);
		CHK_ERR(sd, "accept");
		// close (listen_sd);

		printf ("Connection from %s, port %x\n",
		inet_ntoa( sa_cli.sin_addr ), sa_cli.sin_port);

		/* ----------------------------------------------- */
		/* TCP connection is ready. Do server side SSL. */

		SSL* ssl = SSL_new (ctx);						   CHK_NULL(ssl);
		if ((ssl)==NULL) { printf("CHK_NULL\n"); exit (1); }
		SSL_set_fd (ssl, sd);
		err = SSL_accept (ssl);
		if ((err)==-1) { printf("CHK_ERR\n"); ERR_print_errors_fp(stderr); exit(2); }

		pthread_t ssl_tid = 0;
		if ( !pthread_create( &ssl_tid, NULL, ssl_thread, ssl ) )
		{
			// ssl_thread_running = 1;
			printf( "Thread for client created.\n" );
			threads.push_back(ssl_tid);
		}
		else
		{
			eprintf( "Unable to create thread for client!\n");
		}
	}

	for (pthread_t thread: threads) {
		pthread_join( thread, NULL );
		printf("Thread %ld joined.\n", thread);
	}

	SSL_CTX_free (ctx);
}
/* EOF - ssl_srv.cpp */
