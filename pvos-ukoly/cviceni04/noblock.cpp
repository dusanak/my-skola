#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
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
    int flg = fcntl( 0, F_GETFL );
    fcntl( 0, F_SETFL, flg | O_NONBLOCK );

    timeval start;
    gettimeofday(&start, nullptr);

    size_t i = 0;
    while (true) {      
        int ret;
        ret = read( 0, temp_buf + i, 1 );
        if (ret > 0) {
            if (temp_buf[i] == '\n') {
                temp_buf[i] = '\0';
                break;
            }
            i++;
        } else {
            timeval current;
            gettimeofday(&current, nullptr);
            int diff = (current.tv_sec - start.tv_sec) * 1000000 + (current.tv_usec - start.tv_usec);

            if (diff > us_tout) {
                printf ( "Timeout\n" );
                return -1;
            }
            usleep(10000); // 10 ms
        }
    }

    memcpy(buf, temp_buf, i * sizeof(char));
    return i;
}
