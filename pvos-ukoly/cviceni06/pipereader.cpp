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

int readline( int fd, void *buf, size_t len, int us_tout ) {
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

        int ret;
        ret = poll(&pfd, 1, (us_tout - diff) / 1000);

        if ( ret == 0 ) { 
            printf ( "Timeout\n" );
            return -1;
        }

        if ( pfd.revents & POLLIN )
        {
            printf("Reading\n");
            ret = read( 0 , temp_buf + i, 1 );

            if (temp_buf[i] == '\0') {
                printf("End of string\n");
            }

            if (temp_buf[i] == '\n') {
                printf("End of line\n");
                temp_buf[i] = '\0';
                break;
            }
        }
    }

    memcpy(buf, temp_buf, i * sizeof(char));
    return i;
}

int main()
{
    int fd = open("randompipe", O_RDONLY);
    fcntl( fd, F_SETFL, fcntl( fd, F_GETFL ) & ~O_NONBLOCK );
    printf("Pipe open %d\n", fd);

    while ( 1 )
    {
        char buf[BUFFER_SIZE];
        int ret = readline(fd, buf, BUFFER_SIZE, 5000000); // 5 s
        if (ret > 0)
        {
            printf("%s\n", buf);
            usleep( 100000 );
            continue;
        }
    }
}
