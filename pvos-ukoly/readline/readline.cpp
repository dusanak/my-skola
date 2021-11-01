#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <poll.h>
#include <string.h>
#include <sys/time.h>

#define BUFFER_SIZE 1024

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

    size_t i;
    for (i = 0; i < len - 1; i++ ) {    
        timeval current;
        gettimeofday(&current, nullptr);

        if (timercmp(&current, &end, >=)) {
            printf ( "Timeout\n" );
            return -1;
        }

        timersub(&end, &current, &diff);

        int ret = poll(&pfd, 1, diff.tv_sec * 1000000 + diff.tv_usec);

        if ( ret == 0 ) { 
            printf ( "Timeout\n" );
            return -1;
        }

        if ( pfd.revents & POLLIN )
        {
            //printf("Reading\n");
            ret = read(fd, internal_buf + i, 1);

            if (internal_buf[i] == '\0') {
                printf("End of string\n");
                break;
            }

            if (internal_buf[i] == '\n') {
                printf("End of line\n");
                internal_buf[i] = '\0';
                break;
            }
        }

        //printf("%d, %d\n", i, diff.tv_sec * 1000000 + diff.tv_usec);
    }
    
    if ((i == (len - 2)) && (internal_buf[len - 2] != '\0')) {
        internal_buf[len - 1] = '\0';
    } 

    memset(buf, 0, BUFFER_SIZE);
    memcpy(buf, internal_buf, i * sizeof(char));

    free(internal_buf);

    return i;
}

int main() {
    char text_buffer[BUFFER_SIZE];

    readline(0, text_buffer, BUFFER_SIZE, 10000000); // 10 s

    printf("%s\n", text_buffer);

    for (size_t i = 0; i < strlen(text_buffer) + 1; i++) {
        printf("%c: %d, ", text_buffer[i], (int)text_buffer[i]);
    }
    printf("\n");

    return 0;
}