#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <poll.h>
#include <string.h>
#include <sys/time.h>

#define MAX_LENGTH 1024
#define BUFFER_SIZE 512

int readline(int fd, void *buf, size_t len, int us_tout) {
    char temp_buf[BUFFER_SIZE];
    pollfd pfd;
    pfd.fd = fd;
    pfd.events = POLLIN;

    timeval start;
    gettimeofday(&start, nullptr);

    size_t i;
    for (i = 0; i < len; i++ ) {    
        timeval current;
        gettimeofday(&current, nullptr);

        int diff = (current.tv_sec - start.tv_sec) * 1000000 + (current.tv_usec - start.tv_usec);

        int ret = poll(&pfd, 1, (us_tout - diff) / 1000);

        if ( ret == 0 ) { 
            //printf ( "Timeout\n" );
            return -1;
        }

        if ( pfd.revents & POLLIN )
        {
            //printf("Reading\n");
            ret = read(fd, temp_buf + i, 1);

            if (temp_buf[i] == '\0') {
                //printf("End of string\n");
                break;
            }

            if (temp_buf[i] == '\n') {
                //printf("End of line\n");
                temp_buf[i] = '\0';
                break;
            }
        }
    }

    memset(buf, 0, BUFFER_SIZE);
    memcpy(buf, temp_buf, i * sizeof(char));
    return i;
}

bool check_sum(int* numbers, int len) {
    int sum = 0;

    for (int i = 0; i < len - 1; i++) {
        sum += numbers[i];
    }

    return sum == numbers[len - 1];
}

int main()
{
    int fd = open("randompipe", O_RDONLY);
    printf("Pipe open %d\n", fd);

    while ( 1 )
    {
        char buf[BUFFER_SIZE];
        int ret = readline(fd, buf, BUFFER_SIZE, 5000000); // 5 s
        if (ret > 0)
        {
            int out[BUFFER_SIZE];

            int read_bytes = 0;
            int index = 0;

            while (read_bytes < ret) {
                int tmp_bytes = 0;
                sscanf(buf + read_bytes, "%d%n", out + index, &tmp_bytes);
                read_bytes += tmp_bytes;
                index += 1;
            }

            /*printf("Result: ");
            for (int i = 0; i < index; i++) {
                printf("%d ", out[i]);
            }
            printf("\n");*/

            if (!check_sum(out, index)) {
                printf("Chybny soucet!: ");
                for (int i = 0; i < index; i++) {
                    printf("%d ", out[i]);
                }
                printf("\n");
            }

            usleep( 100000 );
        } else {
            printf("Nudim se!\n");
        }
    }
}
