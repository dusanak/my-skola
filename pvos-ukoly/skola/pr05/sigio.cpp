#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/wait.h>

int fd = 0;

void sigio( int sig )
{
    printf( "mam signal %d\n", sig );
    char buf[ 128 ];
    int r = read( fd, buf, sizeof( buf ) );
    if ( r > 0 )
        write( 1, buf, r );
}

void sigio2( int sig, siginfo_t *si, void * )
{
    printf( "mam signal2 %d\n", sig );
}

int main()
{

    struct sigaction sa;
    //sa.sa_handler = sigio;
    sa.sa_sigaction = sigio2;
    sa.sa_flags = SA_SIGINFO;
    sigemptyset( &sa.sa_mask );


    int roura[ 2 ];
    pipe( roura );
    fd = roura[ 0 ];

    if ( fork() == 0 )
    {
        while ( 1 )
        {
            char buf[ 256 ];
            sprintf( buf, "(%d) %d\n", getpid(), rand() % 1000 );
            write( roura[ 1 ], buf, strlen( buf ) );
            sleep( 1 );
        }
    }

    sigaction( SIGIO, &sa, nullptr );

    int flg = fcntl( fd, F_GETFL );
    fcntl( fd, F_SETFL, flg | O_ASYNC );
    fcntl( fd, F_SETSIG, SIGIO );
    fcntl( fd, F_SETOWN, getpid() );

    while( 1 ) wait( nullptr );
    //while ( 1 ) sleep( 1 );

}

