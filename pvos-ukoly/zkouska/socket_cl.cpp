//***************************************************************************
//
// Program example for subject Operating Systems
//
// Petr Olivka, Dept. of Computer Science, petr.olivka@vsb.cz, 2021
//
// Example of socket server/client.
//
// This program is example of socket client.
// The mandatory arguments of program is IP adress or name of server and
// a port number.
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
#include <netdb.h>

#define STR_CLOSE               "close"

//***************************************************************************
// log messages

#define LOG_ERROR               0       // errors
#define LOG_INFO                1       // information and notifications
#define LOG_DEBUG               2       // debug messages

//***************************************************************************
// communication

#define INIT_REQ "INIT %s %d\n"
#define INIT_RESP "INIT-OK\n"
#define UP_REQ "UP %s\n"
#define UP_RESP "UP-OK\n"
#define DOWN_REQ "DOWN %s\n"
#define DOWN_RESP "DOWN-OK\n"
#define UNLINk_REQ "UNLINK %s\n"
#define UNLINK_RESP "UNLINK-OK\n"
#define ERR_RESP "ERR\n"


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
            "  Socket client example.\n"
            "\n"
            "  Use: %s [-h -d] ip_or_name port_number SEM-NAME [INITVAL]\n"
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

int main( int t_narg, char **t_args )
{

    if ( t_narg <= 2 ) help( t_narg, t_args );

    char *l_host = nullptr;
    int l_port = 0;
    char *sem_name = nullptr;
    int init_val = -1;

    // parsing arguments
    for ( int i = 1; i < t_narg; i++ )
    {
        if ( !strcmp( t_args[ i ], "-d" ) )
            g_debug = LOG_DEBUG;

        if ( !strcmp( t_args[ i ], "-h" ) )
            help( t_narg, t_args );

        if ( *t_args[ i ] != '-' )
        {
            if ( !l_host )
                l_host = t_args[ i ];
            else if ( !l_port )
                l_port = atoi( t_args[ i ] );
            else if ( !sem_name )
                sem_name = t_args[ i ];
            else if ( init_val < 0 )
                init_val = atoi( t_args[ i ]);
        }
    }

    if ( !l_host || !l_port || !sem_name )
    {
        log_msg( LOG_INFO, "Host or port or sem_name is missing!" );
        help( t_narg, t_args );
        exit( 1 );
    }

    log_msg( LOG_INFO, "Connection to '%s':%d.", l_host, l_port );

    addrinfo l_ai_req, *l_ai_ans;
    bzero( &l_ai_req, sizeof( l_ai_req ) );
    l_ai_req.ai_family = AF_INET;
    l_ai_req.ai_socktype = SOCK_STREAM;

    int l_get_ai = getaddrinfo( l_host, nullptr, &l_ai_req, &l_ai_ans );
    if ( l_get_ai )
    {
        log_msg( LOG_ERROR, "Unknown host name!" );
        exit( 1 );
    }

    sockaddr_in l_cl_addr =  *( sockaddr_in * ) l_ai_ans->ai_addr;
    l_cl_addr.sin_port = htons( l_port );
    freeaddrinfo( l_ai_ans );

    // socket creation
    int l_sock_server = socket( AF_INET, SOCK_STREAM, 0 );
    if ( l_sock_server == -1 )
    {
        log_msg( LOG_ERROR, "Unable to create socket.");
        exit( 1 );
    }

    // connect to server
    if ( connect( l_sock_server, ( sockaddr * ) &l_cl_addr, sizeof( l_cl_addr ) ) < 0 )
    {
        log_msg( LOG_ERROR, "Unable to connect server." );
        exit( 1 );
    }

    uint l_lsa = sizeof( l_cl_addr );
    // my IP
    getsockname( l_sock_server, ( sockaddr * ) &l_cl_addr, &l_lsa );
    log_msg( LOG_INFO, "My IP: '%s'  port: %d",
             inet_ntoa( l_cl_addr.sin_addr ), ntohs( l_cl_addr.sin_port ) );
    // server IP
    getpeername( l_sock_server, ( sockaddr * ) &l_cl_addr, &l_lsa );
    log_msg( LOG_INFO, "Server IP: '%s'  port: %d",
             inet_ntoa( l_cl_addr.sin_addr ), ntohs( l_cl_addr.sin_port ) );

    log_msg( LOG_INFO, "Enter 'close' to close application." );

    char l_buf[ 128 ];
    int l_len = -1;

    if (init_val >= 0) {
        printf("Sending init request!\n");
        l_len = sprintf(l_buf, INIT_REQ, sem_name, init_val);
        l_len = write( l_sock_server, l_buf, l_len);
        if ( l_len < 0 ) {
            log_msg( LOG_ERROR, "Unable to send data to server." );
            return 1;
        }
        else
            log_msg( LOG_DEBUG, "Sent %d bytes to server.", l_len );

        // read data from server
        int l_len = read( l_sock_server, l_buf, sizeof( l_buf ) );
        if ( !l_len )
        {
            log_msg( LOG_DEBUG, "Server closed socket." );
            return 1;
        }
        else if ( l_len < 0 )
            log_msg( LOG_DEBUG, "Unable to read data from server." );
        else
            log_msg( LOG_DEBUG, "Read %d bytes from server.", l_len );

        // display on stdout
        l_len = write( STDOUT_FILENO, l_buf, l_len );
        if ( l_len < 0 )
            log_msg( LOG_ERROR, "Unable to write to stdout." );

        // l_buf[l_len] = 0;
        // if (!strcmp(l_buf, INIT_RESP))
        //     printf("Correct response!\n");

        // if (!strcmp(l_buf, ERR_RESP))
        //     printf("Error response!\n");            
    }

    // go!
    while ( 1 )
    {
        char l_buf[ 128 ];
        int l_len;

        // down request
        printf("Sending down request!\n");
        l_len = sprintf(l_buf, DOWN_REQ, sem_name);
        l_len = write( l_sock_server, l_buf, l_len);
        if ( l_len < 0 ) {
            log_msg( LOG_ERROR, "Unable to send data to server." );
            return 1;
        }
        else
            log_msg( LOG_DEBUG, "Sent %d bytes to server.", l_len );

        // read data from server
        l_len = read( l_sock_server, l_buf, sizeof( l_buf ) );
        if ( !l_len )
        {
            log_msg( LOG_DEBUG, "Server closed socket." );
            break;
        }
        else if ( l_len < 0 )
            log_msg( LOG_DEBUG, "Unable to read data from server." );
        else
            log_msg( LOG_DEBUG, "Read %d bytes from server.", l_len );

        // display on stdout
        l_len = write( STDOUT_FILENO, l_buf, l_len );
        if ( l_len < 0 )
            log_msg( LOG_ERROR, "Unable to write to stdout." ); 

        // some work
        for (int i = 0; i < 10; i++) {
            printf("%d\n", i);
            usleep(200000);
        }

        // up request
        printf("Sending up request!\n");
        l_len = sprintf(l_buf, UP_REQ, sem_name);
        l_len = write( l_sock_server, l_buf, l_len);
        if ( l_len < 0 ) {
            log_msg( LOG_ERROR, "Unable to send data to server." );
            return 1;
        }
        else
            log_msg( LOG_DEBUG, "Sent %d bytes to server.", l_len );

        // read data from server
        l_len = read( l_sock_server, l_buf, sizeof( l_buf ) );
        if ( !l_len )
        {
            log_msg( LOG_DEBUG, "Server closed socket." );
            break;
        }
        else if ( l_len < 0 )
            log_msg( LOG_DEBUG, "Unable to read data from server." );
        else
            log_msg( LOG_DEBUG, "Read %d bytes from server.", l_len );

        // display on stdout
        l_len = write( STDOUT_FILENO, l_buf, l_len );
        if ( l_len < 0 )
            log_msg( LOG_ERROR, "Unable to write to stdout." ); 
    }

    // close socket
    close( l_sock_server );

    return 0;
  }
