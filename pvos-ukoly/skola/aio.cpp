#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>
#include <aio.h>

#define BUFFER_SIZE 1024

int main () {

    int fd = 0;

    volatile char buf[BUFFER_SIZE];

    aiocb aio;
    aio.aio_fildes = fd;
    aio.aio_offset = 0;
    aio.aio_buf = buf;
    aio.aio_nbytes = sizeof(buf);
    aio.aio_sigevent.sigev_notify = SIGEV_NONE;
    aio.aio_lio_opcode = 0;
    aio.aio_reqprio = 0;

    int ret = aio_read(&aio);
    printf("%d\n", ret);

    while (1) {
        if (aio_error(&aio) == 0) {
            int l = aio_return(&aio);
            
            if (l > 0) {
                write(1, (void *) aio.aio_buf, l);
                //break;
            }

            aio_read(&aio);
        }
        sleep(1);
    }

    return 0;
}
