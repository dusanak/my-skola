#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
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

    while ( 1 )
    {
        char buf[ 256 ];
        int ret;

        fd_set pro_cteni;
        FD_ZERO( &pro_cteni );
        FD_SET( 0, &pro_cteni );
        FD_SET( roura1[ 0 ], &pro_cteni );
        FD_SET( roura2[ 0 ], &pro_cteni );
        int max = MAX( roura1[ 0 ], roura2[ 0 ] );

        timeval tout = { 0, 500000 };

        ret = select( max + 1, &pro_cteni, nullptr, nullptr, &tout );

        if ( ret == 0 ) printf ( "nuda...\n" );
        else printf( "tout %d:%06d\n", tout.tv_sec, tout.tv_usec );

        if ( FD_ISSET( roura1[ 0 ], &pro_cteni ) )
        {
            ret = read( roura1[ 0 ], buf, sizeof( buf ) );
            if ( ret > 0 ) write( 1, buf, ret );
        }
        if ( FD_ISSET( roura2[ 0 ], &pro_cteni ) )
        {
            ret = read( roura2[ 0 ], buf, sizeof( buf ) );
            if ( ret > 0 ) write( 1, buf, ret );
        }

        if ( FD_ISSET( 0, &pro_cteni ) )
        {
            ret = read( 0, buf, sizeof( buf ) );
            if ( ret > 0 )
            {
                if ( strncmp( buf, "quit", 4 ) == 0 ) return 0;
            }
        }
            

    }
}
