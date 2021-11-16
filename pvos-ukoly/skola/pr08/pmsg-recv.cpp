#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/ipc.h>
//#include <sys/msg.h>
#include <fcntl.h>           /* For O_* constants */
#include <sys/stat.h>        /* For mode constants */
#include <mqueue.h>


struct MyMsg 
{
    long hdr;
    union 
    {
        double d;
        long l;
    };
};

#define MyMsgSize ( sizeof( MyMsg ) )


int main()
{
    int msgfd = mq_open( "/fronta", O_RDWR );
    printf( "msg fd: %d\n", msgfd );

    mq_attr mqatr;
    mq_getattr( msgfd, &mqatr );
    printf( "Fronta %ld %ld \n", mqatr.mq_maxmsg, mqatr.mq_msgsize );

    while ( 1 )
    {
        char types[] = "dl";
        char buf[ mqatr.mq_msgsize ];
        MyMsg *msg = ( MyMsg * ) buf;

        unsigned prio;
        int ret = mq_receive( msgfd, buf, sizeof( buf ), &prio );
        if ( ret < 0 )
        {
            printf( "Polamana fronta...%s\n", strerror( errno ) );
            continue;
        }
        printf(  "Prisla zprava %c : %d \n", ( char ) msg->hdr, ret );

        if ( msg->hdr == types[ 0 ] )
        {
            printf( "Mam zpravu %c : %f\n", ( char ) msg->hdr, msg->d );
        }
        else
        {
            printf( "Mam zpravu %c : %ld\n", ( char ) msg->hdr, msg->l );
        }

    }
}
