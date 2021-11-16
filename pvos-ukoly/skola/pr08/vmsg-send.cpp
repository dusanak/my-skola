#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>

struct MyMsg 
{
    long hdr;
    union 
    {
        double d;
        long l;
    };
};

#define MyMsgSize ( sizeof( MyMsg ) - sizeof( long ) )


int main()
{
    int msgid = msgget( 0xbeef, 0600 | IPC_CREAT );
    printf( "msg id: %d\n", msgid );


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

        int ret = msgsnd( msgid, &msg, MyMsgSize, 0 );
        printf( "Odeslani zpravy %c : %d\n", types[ choice ], ret );
    }
}
