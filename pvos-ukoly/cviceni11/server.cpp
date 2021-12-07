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
#include <vector>
#include <pthread.h>
#include <semaphore.h>

#define STR_CLOSE   "close"
#define STR_QUIT    "quit"

//***************************************************************************
// log messages

#define LOG_ERROR               0       // errors
#define LOG_INFO                1       // information and notifications
#define LOG_DEBUG               2       // debug messages

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

void *client_handler(int sock_pair) {    
    // list of fd sources
    std::vector<pollfd> l_read_poll;

    pollfd sock_p;
    sock_p.fd = sock_pair;
    sock_p.events = POLLIN; 

    l_read_poll.push_back(sock_p);

    while ( 1 )
    { // communication
        char l_buf[ 256 ];

        // select from fds
        int l_poll = poll( l_read_poll.data(), l_read_poll.size(), -1 );

        if ( l_poll < 0 )
        {
            log_msg( LOG_ERROR, "Function poll failed!" );
            exit( 1 );
        }

        if ( l_read_poll[ 0 ].revents & POLLIN )
        {
            msghdr msg;
            msg.msg_name = nullptr;
            msg.msg_namelen = 0;
        
            int recvpid;
            iovec ivec;
            ivec.iov_base = &recvpid;
            ivec.iov_len = sizeof( recvpid );

            msg.msg_iov = &ivec;
            msg.msg_iovlen = 1;

            char msgbuf[ CMSG_SPACE( sizeof( int ) ) ];
            cmsghdr *cmsg = ( cmsghdr * ) msgbuf;
            cmsg->cmsg_len = CMSG_LEN( sizeof( int ) );
            
            msg.msg_control = cmsg;
            msg.msg_controllen = CMSG_SPACE( sizeof( int ) );
            
            msg.msg_flags = 0;

            int ret = recvmsg( l_read_poll[ 0 ].fd, &msg, 0 );
            if ( ret < 0 )
                printf( "recvmsg error %d (%s)\n", errno, strerror( errno ) );

            int fd = * ( int * ) CMSG_DATA( cmsg );

            printf( "(%d) received fd %d\n", recvpid, fd );

            pollfd new_client;
            new_client.fd = fd;
            new_client.events = POLLIN | POLLPRI;
            l_read_poll.push_back(new_client);
        }

        for (size_t i = 1; i < l_read_poll.size(); i++) {
            // data from client?
            int l_sock_client = l_read_poll[ i ].fd;
            if ( l_read_poll[ i ].revents & POLLPRI )
            {
                char oob_msg;
                int l_ret = recv( l_sock_client, &oob_msg, 1, MSG_OOB );
                if ( l_ret <= 0 )
                    log_msg( LOG_ERROR, "OOB recv failed.\n" );
                else
                    printf( "OOB %c\n", oob_msg );
                    
                log_msg( LOG_INFO, "Client sent 'close' OOB request to close connection." );
                close( l_sock_client );
                l_read_poll.erase(l_read_poll.begin() + i);
                log_msg( LOG_INFO, "Connection closed." );
            }

            else if ( l_read_poll[ i ].revents & POLLIN )
            {
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

                // close request?
                if ((!strncasecmp( l_buf, "close", strlen( STR_CLOSE ))))
                {
                    log_msg( LOG_INFO, "Client sent 'close' request to close connection." );
                    close( l_sock_client );
                    l_read_poll.erase(l_read_poll.begin() + i);
                    log_msg( LOG_INFO, "Connection closed." );
                }

                // display on stdout
                l_len = write( STDOUT_FILENO, l_buf, l_len );
                if ( l_len < 0 )
                    log_msg( LOG_ERROR, "Unable to write to stdout." );

                // sending received message to all other clients
                for (auto client: l_read_poll) {
                    int _l_sock_client = client.fd;

                    if(_l_sock_client != l_sock_client) {
                        l_len = write( _l_sock_client, l_buf, l_len );
                        if ( l_len < 0 )
                            log_msg( LOG_ERROR, "Unable to write data to stdout." );
                    }
                }
            }
        }
        // request for quit
    } // while communication
    
    return nullptr;
}


int main( int t_narg, char **t_args )
{
    if ( t_narg <= 1 ) help( t_narg, t_args );

    int l_port = 0;
    int l_sock_listen;

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
    l_sock_listen = socket( AF_INET, SOCK_STREAM, 0 );
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
    // go!

    int spair[ 2 ];
    socketpair( AF_UNIX, SOCK_STREAM, 0, spair );

    if (fork() > 0) {
        while ( 1 )
        {
            int l_sock_client = -1;

            // list of fd sources
            pollfd l_read_poll[ 2 ];

            l_read_poll[ 0 ].fd = STDIN_FILENO;
            l_read_poll[ 0 ].events = POLLIN;
            l_read_poll[ 1 ].fd = l_sock_listen;
            l_read_poll[ 1 ].events = POLLIN;

            while ( 1 ) // wait for new client
            {
                // select from fds
                int l_poll = poll( l_read_poll, 2, -1 );

                if ( l_poll < 0 )
                {
                    log_msg( LOG_ERROR, "Function poll failed!" );
                    exit( 1 );
                }

                if ( l_read_poll[ 0 ].revents & POLLIN )
                { // data on stdin
                    char buf[ 128 ];
                    int len = read( STDIN_FILENO, buf, sizeof( buf) );
                    if ( len < 0 )
                    {
                        log_msg( LOG_DEBUG, "Unable to read from stdin!" );
                        exit( 1 );
                    }

                    log_msg( LOG_DEBUG, "Read %d bytes from stdin" );
                    // request to quit?
                    if ( !strncmp( buf, STR_QUIT, strlen( STR_QUIT ) ) )
                    {
                        log_msg( LOG_INFO, "Request to 'quit' entered.");
                        close( l_sock_listen );
                        exit( 0 );
                    }
                }

                if ( l_read_poll[ 1 ].revents & POLLIN )
                { // new client?
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

                    msghdr msg;
                    msg.msg_name = nullptr;
                    msg.msg_namelen = 0;
                
                    int mypid = getpid();
                    iovec ivec;
                    ivec.iov_base = &mypid;
                    ivec.iov_len = sizeof( mypid );

                    msg.msg_iov = &ivec;
                    msg.msg_iovlen = 1;

                    char msgbuf[ CMSG_SPACE( sizeof( int ) ) ];
                    cmsghdr *cmsg = ( cmsghdr * ) msgbuf;
                    cmsg->cmsg_len = CMSG_LEN( sizeof( int ) );
                    cmsg->cmsg_level = SOL_SOCKET;
                    cmsg->cmsg_type = SCM_RIGHTS;
                    * ( int * ) CMSG_DATA( cmsg ) = l_sock_client;

                    msg.msg_control = cmsg;
                    msg.msg_controllen = CMSG_SPACE( sizeof( int ) );
                    
                    msg.msg_flags = 0;

                    printf( "(%d) sending descriptor %d\n", getpid(), l_sock_client );

                    int ret = sendmsg( spair[ 1 ], &msg, 0 );
                    if ( ret < 0 )
                        printf( "sendmsg error %d (%s)\n", errno, strerror( errno ) );
                }

            } // while wait for client
            
        } // while ( 1 )
    } else {
        client_handler(spair[0]);
    }

    return 0;
}
