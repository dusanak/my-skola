//***************************************************************************
//
// Program example for labs in subject Operating Systems
//
// Petr Olivka, Dept. of Computer Science, petr.olivka@vsb.cz, 2017
//
// Example of socket server.
//
// This program is example of socket server and it allows to connect and serve
// the only one client.
// The mandatory argument of program is port number for listening.
//
//***************************************************************************

#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <stdarg.h>
#include <poll.h>
#include <sys/socket.h>
#include <sys/param.h>
#include <sys/time.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <errno.h>
#include <wait.h>
#include <sys/mman.h>
#include <semaphore.h>

#define STR_CLOSE   "close"
#define STR_QUIT    "quit"

//***************************************************************************
// log messages

#define LOG_ERROR               0       // errors
#define LOG_INFO                1       // information and notifications
#define LOG_DEBUG               2       // debug messages

//***************************************************************************
// communication

#define INIT_REQ "INIT"
#define INIT_RESP "INIT-OK\n"
#define UP_REQ "UP"
#define UP_RESP "UP-OK\n"
#define DOWN_REQ "DOWN"
#define DOWN_RESP "DOWN-OK\n"
#define UNLINK_REQ "UNLINK"
#define UNLINK_RESP "UNLINK-OK\n"
#define ERR_RESP "ERR\n"

#define MAX_SEMAPHORES 64

// debug flag
int g_debug = LOG_INFO;

void log_msg( int t_log_level, const char *t_form, ... )
{
    const char *out_fmt[] = {
            "ERR: (%d-%s) %s\n",
            "INF: %s\n",
            "DEB: %s\n" };

    if ( t_log_level && t_log_level > g_debug ) return;

    char l_buf[ 1024 ];
    va_list l_arg;
    va_start( l_arg, t_form );
    vsprintf( l_buf, t_form, l_arg );
    va_end( l_arg );

    switch ( t_log_level )
    {
    case LOG_INFO:
    case LOG_DEBUG:
        fprintf( stdout, out_fmt[ t_log_level ], l_buf );
        break;

    case LOG_ERROR:
        fprintf( stderr, out_fmt[ t_log_level ], errno, strerror( errno ), l_buf );
        break;
    }
}

//***************************************************************************
// help

void help( int t_narg, char **t_args )
{
    if ( t_narg <= 1 || !strcmp( t_args[ 1 ], "-h" ) )
    {
        printf(
            "\n"
            "  Socket server example.\n"
            "\n"
            "  Use: %s [-h -d] port_number\n"
            "\n"
            "    -d  debug mode \n"
            "    -h  this help\n"
            "\n", t_args[ 0 ] );

        exit( 0 );
    }

    if ( !strcmp( t_args[ 1 ], "-d" ) )
        g_debug = LOG_DEBUG;
}

//***************************************************************************

struct sem_info {
    char semaphore_name[128];
    sem_t semaphore;
};

int* number_of_semaphores;
sem_info* semaphores;


int semaphore_init(const char * name, int val) {
    for (int i = 0; i < *number_of_semaphores; i++) {
        if (!strcmp(name, semaphores[i].semaphore_name)) {
            printf("Semaphore %s exists!\n", name);
            return 1;
        }
    }

    sem_init(&semaphores[*number_of_semaphores].semaphore, 1, val);
    strcpy(semaphores[*number_of_semaphores].semaphore_name, name);

    *number_of_semaphores += 1;
    return 0;
}

int semaphore_down(const char * name) {
    int pos = -1;
    for (int i = 0; i < *number_of_semaphores; i++) {
        if (!strcmp(name, semaphores[i].semaphore_name)) {
            pos = i;
            break;
        }
    }
    if (pos < 0) {
        printf("Semaphore %s does not exist!\n", name);
        return 1;
    }

    sem_wait(&semaphores[pos].semaphore);
    return 0;
}

int semaphore_up(const char * name) {
    int pos = -1;
    for (int i = 0; i < *number_of_semaphores; i++) {
        if (!strcmp(name, semaphores[i].semaphore_name)) {
            pos = i;
            break;
        }
    }
    if (pos < 0) {
        printf("Semaphore %s does not exist!\n", name);
        return 1;
    }

    sem_post(&semaphores[pos].semaphore);
    return 0;
}

// NOT IMPLEMENTED
int semaphore_unlink(const char * name) {
    return 1;
}


int fork_server(int l_sock_client) {
    while ( 1 )
    { // communication
        char l_buf[ 256 ];
        char name[ 64 ];

        // read data from socket
        int l_len = read( l_sock_client, l_buf, sizeof( l_buf ) );
        if ( !l_len )
        {
            log_msg( LOG_DEBUG, "Client closed socket!" );
            close( l_sock_client );
            break;
        }
        else if ( l_len < 0 )
            log_msg( LOG_DEBUG, "Unable to read data from client." );
        else
            log_msg( LOG_DEBUG, "Read %d bytes from client.", l_len );

        // write data from client
        l_len = write( STDOUT_FILENO, l_buf, l_len );
        if ( l_len < 0 )
            log_msg( LOG_ERROR, "Unable to write data to stdout." );

        if (!strncmp(l_buf, INIT_REQ, strlen(INIT_REQ))) 
        {
            printf("Init request!\n");
            
            int val = -1;
            sscanf(l_buf, "INIT %s %d\n", name, &val);

            printf("%s: %d\n", name, val);

            semaphore_init(name, val);
            
            printf("Sending init response!\n");
            l_len = write( l_sock_client, INIT_RESP, strlen(INIT_RESP));
            if ( l_len < 0 ) {
                log_msg( LOG_ERROR, "Unable to send data to server." );
                return 1;
            }
            else
                log_msg( LOG_DEBUG, "Sent %d bytes to server.", l_len );
        }
        else if (!strncmp(l_buf, DOWN_REQ, strlen(DOWN_REQ)))
        {
            printf("Down request!\n");

            sscanf(l_buf, "DOWN %s\n", name);

            printf("%s\n", name);

            semaphore_down(name);
            
            printf("Sending down response!\n");
            l_len = write( l_sock_client, DOWN_RESP, strlen(DOWN_RESP));
            if ( l_len < 0 ) {
                log_msg( LOG_ERROR, "Unable to send data to server." );
                return 1;
            }
            else
                log_msg( LOG_DEBUG, "Sent %d bytes to server.", l_len );
        }
        else if (!strncmp(l_buf, UP_REQ, strlen(UP_REQ)))
        {
            printf("Up request!\n");

            sscanf(l_buf, "UP %s\n", name);

            printf("%s\n", name);

            semaphore_up(name);
            
            printf("Sending up response!\n");
            l_len = write( l_sock_client, UP_RESP, strlen(UP_RESP));
            if ( l_len < 0 ) {
                log_msg( LOG_ERROR, "Unable to send data to server." );
                return 1;
            }
            else
                log_msg( LOG_DEBUG, "Sent %d bytes to server.", l_len );
        } else if (!strncmp(l_buf, UNLINK_REQ, strlen(UNLINK_REQ)))
        {
            printf("Unlink request!\n");

            sscanf(l_buf, "UNLINK %s\n", name);

            printf("%s\n", name);

            semaphore_unlink(name);
            
            printf("Sending unlink response!\n");
            l_len = write( l_sock_client, UNLINK_RESP, strlen(UNLINK_RESP));
            if ( l_len < 0 ) {
                log_msg( LOG_ERROR, "Unable to send data to server." );
                return 1;
            }
            else
                log_msg( LOG_DEBUG, "Sent %d bytes to server.", l_len );
        } else {
            printf("Bad request!\n");
            printf("Sending err response!\n");
            l_len = write( l_sock_client, ERR_RESP, strlen(ERR_RESP));
            if ( l_len < 0 ) {
                log_msg( LOG_ERROR, "Unable to send data to server." );
                return 1;
            }
            else
                log_msg( LOG_DEBUG, "Sent %d bytes to server.", l_len );
        }
    } // while communication

    close( l_sock_client );
    return 0;
}

int main( int t_narg, char **t_args )
{
    if ( t_narg <= 1 ) help( t_narg, t_args );

    int l_port = 0;

    // parsing arguments
    for ( int i = 1; i < t_narg; i++ )
    {
        if ( !strcmp( t_args[ i ], "-d" ) )
            g_debug = LOG_DEBUG;

        if ( !strcmp( t_args[ i ], "-h" ) )
            help( t_narg, t_args );

        if ( *t_args[ i ] != '-' && !l_port )
        {
            l_port = atoi( t_args[ i ] );
            break;
        }
    }

    if ( l_port <= 0 )
    {
        log_msg( LOG_INFO, "Bad or missing port number %d!", l_port );
        help( t_narg, t_args );
    }

    log_msg( LOG_INFO, "Server will listen on port: %d.", l_port );

    // socket creation
    int l_sock_listen = socket( AF_INET, SOCK_STREAM, 0 );
    if ( l_sock_listen == -1 )
    {
        log_msg( LOG_ERROR, "Unable to create socket.");
        exit( 1 );
    }

    in_addr l_addr_any = { INADDR_ANY };
    sockaddr_in l_srv_addr;
    l_srv_addr.sin_family = AF_INET;
    l_srv_addr.sin_port = htons( l_port );
    l_srv_addr.sin_addr = l_addr_any;

    // Enable the port number reusing
    int l_opt = 1;
    if ( setsockopt( l_sock_listen, SOL_SOCKET, SO_REUSEADDR, &l_opt, sizeof( l_opt ) ) < 0 )
      log_msg( LOG_ERROR, "Unable to set socket option!" );

    // assign port number to socket
    if ( bind( l_sock_listen, (const sockaddr * ) &l_srv_addr, sizeof( l_srv_addr ) ) < 0 )
    {
        log_msg( LOG_ERROR, "Bind failed!" );
        close( l_sock_listen );
        exit( 1 );
    }

    // listenig on set port
    if ( listen( l_sock_listen, 1 ) < 0 )
    {
        log_msg( LOG_ERROR, "Unable to listen on given port!" );
        close( l_sock_listen );
        exit( 1 );
    }

    log_msg( LOG_INFO, "Enter 'quit' to quit server." );

    int l_sock_client = -1;

    int fd = shm_open( "/semafory_pocet.dat", O_RDWR | O_CREAT, 0600 );
    ftruncate( fd, 1024 );
    int len = lseek( fd, 0, SEEK_END );
    number_of_semaphores = ( int * ) mmap( nullptr, len, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0 );
    *number_of_semaphores = 0;
    close( fd );

    fd = shm_open( "/semafory.dat", O_RDWR | O_CREAT, 0600 );
    ftruncate( fd, 1024 );
    len = lseek( fd, 0, SEEK_END );
    semaphores = ( sem_info * ) mmap( nullptr, len, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0 );
    close( fd );

    while ( 1 ) // wait for new client
    {
        sockaddr_in l_rsa;
        int l_rsa_size = sizeof( l_rsa );
        // new connection
        l_sock_client = accept( l_sock_listen, ( sockaddr * ) &l_rsa, ( socklen_t * ) &l_rsa_size );
        if ( l_sock_client == -1 )
        {
            log_msg( LOG_ERROR, "Unable to accept new client." );
            close( l_sock_listen );
            exit( 1 );
        }

        int pid = fork();
        if ( !pid )
        {
            fork_server(l_sock_client);
            exit( 0 );
        }

        uint l_lsa = sizeof( l_srv_addr );
        // my IP
        getsockname( l_sock_client, ( sockaddr * ) &l_srv_addr, &l_lsa );
        log_msg( LOG_INFO, "My IP: '%s'  port: %d",
                            inet_ntoa( l_srv_addr.sin_addr ), ntohs( l_srv_addr.sin_port ) );
        // client IP
        getpeername( l_sock_client, ( sockaddr * ) &l_srv_addr, &l_lsa );
        log_msg( LOG_INFO, "Client IP: '%s'  port: %d",
                            inet_ntoa( l_srv_addr.sin_addr ), ntohs( l_srv_addr.sin_port ) );

    } // while ( 1 )
    // wait for children
	int ret_wait;
	while ( ( ret_wait = wait( NULL ) ) != -1 )
		printf( "Child %d finished.\n", ret_wait );

    close( l_sock_listen );
    return 0;
}
