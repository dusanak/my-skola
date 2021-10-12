#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <poll.h>
#include <sys/select.h>
#include <sys/param.h>

int main()
{
    int roura1[ 2 ], roura2[ 2 ];
    pipe( roura1 );
    pipe( roura2 );

    if ( fork() == 0 )
    {
        srand( getpid() );
        while ( 1 )
        {
            char buf[ 128 ];
            sprintf( buf, "(%d) %d\n", getpid(), rand() % 100000 );
            write( roura1[ 1 ], buf, strlen( buf ) );
            sleep( 1 );
        }
        exit( 0 );
    }
    if ( fork() == 0 )
    {
        srand( getpid() );
        while ( 1 )
        {
            char buf[ 128 ];
            sprintf( buf, "(%d) %d\n", getpid(), rand() % 100000 );
            write( roura2[ 1 ], buf, strlen( buf ) );
            sleep( 3 );
        }
        exit( 0 );
    }

    pollfd pfd[ 10 ];
    pfd[ 0 ].fd = 0;
    pfd[ 0 ].events = POLLIN;
    pfd[ 1 ].fd = roura1[ 0 ];
    pfd[ 1 ].events = POLLIN;
    pfd[ 2 ].fd = roura2[ 0 ];
    pfd[ 2 ].events = POLLIN;

    while ( 1 )
    {
        char buf[ 256 ];
        int ret;

        ret = poll( pfd, 3, 500 );

        if ( ret == 0 ) printf ( "nuda...\n" );
        
        if ( pfd[ 1 ].revents & POLLIN )
        {
            ret = read( roura1[ 0 ], buf, sizeof( buf ) );
            if ( ret > 0 ) write( 1, buf, ret );
        }
        if ( pfd[ 2 ].revents & POLLIN )
        {
            ret = read( roura2[ 0 ], buf, sizeof( buf ) );
            if ( ret > 0 ) write( 1, buf, ret );
        }

        if ( pfd[ 0 ].revents & POLLIN )
        {
            ret = read( 0, buf, sizeof( buf ) );
            if ( ret > 0 )
            {
                if ( strncmp( buf, "quit", 4 ) == 0 ) return 0;
            }
        }
            

    }
}
