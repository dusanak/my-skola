#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>

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

    int flg = fcntl( 0, F_GETFL );
    fcntl( 0, F_SETFL, flg | O_NONBLOCK );
    flg = fcntl( roura1[ 0 ], F_GETFL );
    fcntl( roura1[ 0 ], F_SETFL, flg | O_NONBLOCK );
    flg = fcntl( roura2[ 0 ], F_GETFL );
    fcntl( roura2[ 0 ], F_SETFL, flg | O_NONBLOCK );
    while ( 1 )
    {
        char buf[ 256 ];
        int ret;
        ret = read( roura1[ 0 ], buf, sizeof( buf ) );
        if ( ret > 0 ) write( 1, buf, ret );
        ret = read( roura2[ 0 ], buf, sizeof( buf ) );
        if ( ret > 0 ) write( 1, buf, ret );

        ret = read( 0, buf, sizeof( buf ) );
        if ( ret > 0 )
        {
            if ( strncmp( buf, "quit", 4 ) == 0 ) return 0;
        }
            

        usleep( 10000 );
    }
}
