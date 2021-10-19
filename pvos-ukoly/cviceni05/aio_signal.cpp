#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>
#include <aio.h>
#include <fcntl.h>

#define BUFFER_SIZE 1024
#define NUMBER_OF_FORKS 8

#define IO_SIGNAL SIGUSR1

void sigio( int sig, siginfo_t *si, void * )
{

    printf( "Received signal: %d\n", sig );

    struct aiocb * aio = (aiocb *)si->si_value.sival_ptr;
    if (aio_error(aio) == 0) {
        int l = aio_return(aio);
        
        if (l > 0) {
            write(1, (void *) aio->aio_buf, l);
            //break;
        }
    }

    aio_read(aio);
}

int main () {

    int data_pipe[2];
    pipe(data_pipe);
    int fd = data_pipe[0];

    struct sigaction sa;
    sa.sa_sigaction = sigio;
    sa.sa_flags = SA_SIGINFO;
    sigemptyset( &sa.sa_mask );
    sigaction( IO_SIGNAL, &sa, nullptr );

    volatile char buf[BUFFER_SIZE];

    aiocb aio;
    aio.aio_fildes = fd;
    aio.aio_offset = 0;
    aio.aio_buf = buf;
    aio.aio_nbytes = sizeof(buf);
    aio.aio_sigevent.sigev_notify = SIGEV_SIGNAL;
    aio.aio_sigevent.sigev_signo = IO_SIGNAL;
    aio.aio_sigevent.sigev_value.sival_ptr = &aio;
    aio.aio_lio_opcode = 0;
    aio.aio_reqprio = 0;


    for (int i = 0; i < NUMBER_OF_FORKS; i++) {
        if (fork() == 0) {
            srand(getpid());
            while ( 1 ) {
                char buf[ 256 ];
                sprintf( buf, "(%d) %d\n", getpid(), rand() % 1000 );
                write( data_pipe[ 1 ], buf, strlen( buf ) );
                sleep( 1 );
            }
        }
    }

    aio_read(&aio);

    while (1) { sleep(1); }

    return 0;
}
