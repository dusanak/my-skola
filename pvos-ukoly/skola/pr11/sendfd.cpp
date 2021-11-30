// **************************************************************************
//
// Empty C++ project for education
//
// petr.olivka@vsb.cz, Dept. of Computer Science, FEECS, VSB-TU Ostrava, CZ
//
// **************************************************************************

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <sys/types.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <netdb.h>
#include <arpa/inet.h>

int main( int argn, char **arg )
{

    int spair[ 2 ];
    socketpair( AF_UNIX, SOCK_STREAM, 0, spair );

    if ( fork() > 0 ) 
    { //rodic
        printf( "Rodic (%d)\n", getpid() );
        int roura[ 2 ];
        pipe( roura );

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
        * ( int * ) CMSG_DATA( cmsg ) = roura[ 0 ];

        msg.msg_control = cmsg;
        msg.msg_controllen = CMSG_SPACE( sizeof( int ) );
         
        msg.msg_flags = 0;

        printf( "(%d) posila desctiptor %d\n", getpid(), roura[ 0 ] );

        int ret = sendmsg( spair[ 1 ], &msg, 0 );
        if ( ret < 0 )
            printf( "sendmsg error %d (%s)\n", errno, strerror( errno ) );

        char pozdrav[] = "Pozdrav mezi procesy.\n";
        write( roura[ 1 ], pozdrav, strlen( pozdrav ) );

        getchar();
        
        return 0;
    }
    else
    { // potomek
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

        int ret = recvmsg( spair[ 0 ], &msg, 0 );
        if ( ret < 0 )
            printf( "recvmsg error %d (%s)\n", errno, strerror( errno ) );

        int fd = * ( int * ) CMSG_DATA( cmsg );

        printf( "(%d) poslal data mame fd %d\n", recvpid, fd );

        char buf[ 333 ];
        int l = read( fd, buf, sizeof( buf ) );
        write( 1, buf, l );

        exit( 0 );

    }

}   

