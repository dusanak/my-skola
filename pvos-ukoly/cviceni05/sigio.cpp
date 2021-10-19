#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/wait.h>

#define NUMBER_OF_FORKS 8

void sigio( int sig, siginfo_t *si, void * )
{
    printf( "Received signal: %d\n", sig );
    char buf[ 128 ];
    int r = read( si->si_fd, buf, sizeof( buf ) );
    if ( r > 0 )
        write( 1, buf, r );
}

int main()
{

    struct sigaction sa;
    sa.sa_sigaction = sigio;
    sa.sa_flags = SA_SIGINFO;
    sigemptyset( &sa.sa_mask );


    for (int i = 0; i < NUMBER_OF_FORKS; i++) {
        int data_pipe[ 2 ];
        pipe( data_pipe );
        int fd = data_pipe[ 0 ];

        if ( fork() == 0 )
        {
            srand(getpid());
            while ( 1 )
            {
                char buf[ 256 ];
                sprintf( buf, "(%d) %d\n", getpid(), rand() % 1000 );
                write( data_pipe[ 1 ], buf, strlen( buf ) );
                sleep( 1 );
            }
        }

        sigaction( SIGIO, &sa, nullptr );

        int flg = fcntl( fd, F_GETFL );
        fcntl( fd, F_SETFL, flg | O_ASYNC );
        fcntl( fd, F_SETSIG, SIGIO );
        fcntl( fd, F_SETOWN, getpid() );
    }

    while( 1 ) wait( nullptr );
    //while ( 1 ) sleep( 1 );

}

