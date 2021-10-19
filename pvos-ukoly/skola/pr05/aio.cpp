#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>
#include <fcntl.h>
#include <errno.h>
#include <aio.h>
#include <sys/types.h>
#include <sys/wait.h>

int main()
{

    volatile char buf[ 1024 ];
    int fd = 0;

    aiocb aio;
    aio.aio_fildes = fd;
    aio.aio_offset = 0;
    aio.aio_buf = buf;
    aio.aio_nbytes = sizeof( buf );
    aio.aio_reqprio = 0; 
    aio.aio_sigevent.sigev_notify = SIGEV_NONE;
    aio.aio_lio_opcode = 0;

    int ret = aio_read( &aio );
    if ( ret < 0 ) printf( "aio read %d %s\n", ret, strerror( errno ) );

    while ( 1 )
    {

        int ret = aio_error( &aio );
        //if ( ret ) printf( "aio_error %d\n", ret );
        if ( ret == 0 ) // hotovo
        {
            int l = aio_return( &aio );
            if ( l > 0 )
                write( 1, ( void * ) aio.aio_buf, l );

            aio_read( &aio );
        }

        usleep( 10000 );
    }


}

