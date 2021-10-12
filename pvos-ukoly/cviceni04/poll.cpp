#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <poll.h>
#include <sys/time.h>

#define BUFFER_SIZE 512

int readline( int fd, void *buf, size_t len, int us_tout );

int main()
{
    char buf[BUFFER_SIZE];
    int ret = readline(0, buf, BUFFER_SIZE, 10000000); // 10 s

    if (ret > 0) {
        printf("%s\n", buf);
    }

    return 0;
}

int readline( int fd, void *buf, size_t len, int us_tout ) {
    char temp_buf[BUFFER_SIZE];
    pollfd pfd;
    pfd.fd = 0;
    pfd.events = POLLIN;

    timeval start;
    gettimeofday(&start, nullptr);

    size_t i;
    for (i = 0; i < len; i++ ) {    
        timeval current;
        gettimeofday(&current, nullptr);

        int diff = (current.tv_sec - start.tv_sec) * 1000000 + (current.tv_usec - start.tv_usec);

        int ret;
        ret = poll(&pfd, 1, (us_tout - diff) / 1000);

        if ( ret == 0 ) { 
            printf ( "Timeout\n" );
            return -1;
        }

        if ( pfd.revents & POLLIN )
        {
            ret = read( 0 , temp_buf + i, 1 );
            if (temp_buf[i] == '\n') {
                temp_buf[i] = '\0';
                break;
            }
        }
    }

    memcpy(buf, temp_buf, i * sizeof(char));
    return i;
}
