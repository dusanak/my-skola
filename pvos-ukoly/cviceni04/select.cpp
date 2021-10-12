#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/select.h>

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
    fd_set read_only;
    FD_ZERO( &read_only );
    FD_SET( 0, &read_only );
    timeval tout = { 0, us_tout };

    size_t i;
    for (i = 0; i < len; i++ ) {      
        int ret;
        ret = select(1, &read_only, nullptr, nullptr, &tout);

        if ( ret == 0 ) { 
            printf ( "Timeout\n" );
            return -1;
        }

        if ( FD_ISSET( 0, &read_only ) )
        {
            ret = read( 0, temp_buf + i, 1 );
            
            if (temp_buf[i] == '\n') {
                temp_buf[i] = '\0';
                break;
            }
        }
    }

    memcpy(buf, temp_buf, i * sizeof(char));
    return i;
}
