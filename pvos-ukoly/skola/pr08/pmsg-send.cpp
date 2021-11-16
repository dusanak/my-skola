#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
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
    int msgfd = mq_open( "/fronta", O_CREAT | O_RDWR, 0640, nullptr );
    printf( "msg fd: %d\n", msgfd );


    while ( 1 )
    {
        char types[] = "dl";
        int choice = rand() % 2;
        MyMsg msg;

        if ( !choice )
        {
            msg.hdr = types[ choice ];
            msg.d = ( double ) ( rand() % 1000000 ) / 100.0;
            printf( "Posilam %f\n", msg.d );
        }
        else
        {
            msg.hdr = types[ choice ];
            msg.l = rand() % 1000000;
            printf( "Posilam %ld\n", msg.l );
        }

        int ret = mq_send( msgfd, ( char * ) &msg, MyMsgSize, 0 );
        printf( "Odeslani zpravy %c : %d\n", types[ choice ], ret );
    }
}
