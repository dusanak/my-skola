#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <poll.h>
#include <string.h>
#include <errno.h>
#include <sys/time.h>

#define BUFFER_SIZE 16

int readline(int fd, void *buf, size_t len, int us_tout) {
    char* internal_buf = (char*)malloc(sizeof(char) * len);
    pollfd pfd;
    pfd.fd = fd;
    pfd.events = POLLIN;

    timeval diff;
    diff.tv_sec = us_tout / 1000000;
    diff.tv_usec = us_tout % 1000000;

    timeval end;
    gettimeofday(&end, nullptr);
    timeradd(&end, &diff, &end);

    size_t i = 0;
    for (i = 0; i < len - 1; i++ ) {    
        timeval current;
        gettimeofday(&current, nullptr);

        if (timercmp(&current, &end, >=)) {
            free(internal_buf);
            return -1;
        }

        timersub(&end, &current, &diff);

        int ret = poll(&pfd, 1, (diff.tv_sec * 1000) + (diff.tv_usec / 1000));

        if ( ret == 0 ) { 
            free(internal_buf);
            errno = ETIME;
            return -1;
        }

        if (pfd.revents & (POLLIN | POLLHUP)) {
            ret = read(fd, internal_buf + i, 1);

            if (ret == 0) { // EOF
                internal_buf[i] = '\0';
                break;
            }

            if (internal_buf[i] == '\0') {
                break;
            }

            if (internal_buf[i] == '\n') {
                internal_buf[i] = '\0';
                break;
            }
        }
    }
    
    internal_buf[len - 1] = '\0';

    memset(buf, 0, len);
    memcpy(buf, internal_buf, i * sizeof(char));

    free(internal_buf);
    return i;
}

int main() {
    char buf[BUFFER_SIZE];
    int fd = 0;
    int timeout = 5000000; // 10s
    while ( 1 ) {
        int ret = readline( fd, buf, sizeof( buf ), timeout);
        if ( ret < 0 ) {
            if (errno == ETIME) {
                printf("Timeout!\n");
            } else {
                break;
            }
        }
        if ( ret == 0 ) break;
        write( 1, buf, ret );
        sleep(1);
    }

    return 0;
}