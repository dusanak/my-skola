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
    int msgid = msgget( 0xbeef, 0600 );
    printf( "msg id: %d\n", msgid );


    while ( 1 )
    {
        char types[] = "dl";
        MyMsg msg;

        int ret = msgrcv( msgid, &msg, MyMsgSize, 0, 0 );
        if ( ret < 0 )
            printf( "Polamana fronta...\n" );
        printf(  "Prisla zprava %c : %d \n", ( char ) msg.hdr, ret );

        if ( msg.hdr == types[ 0 ] )
        {
            printf( "Mam zpravu %c : %f\n", ( char ) msg.hdr, msg.d );
        }
        else
        {
            printf( "Mam zpravu %c : %ld\n", ( char ) msg.hdr, msg.l );
        }

    }
}
