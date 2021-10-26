#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <poll.h>

int main()
{
    int fd = open( "roura", O_RDWR | O_NONBLOCK );

    printf( "mam otevreno %d\n", fd );

    fcntl( fd, F_SETFL, fcntl( fd, F_GETFL ) & ~O_NONBLOCK );

    write( fd, "Nazdar\n", 7 );

    return 0;

    while ( 1 )
    {
        char buf[ 128 ];
        pollfd pfd = { fd, POLLIN, 0 };
        poll( &pfd, 1, -1 );
        int len = read( fd, buf, sizeof( buf ) );
        if ( len == 0 )
        {
            usleep( 100000 );
            continue;
        }
        write( 1, buf, len );
    }
}
