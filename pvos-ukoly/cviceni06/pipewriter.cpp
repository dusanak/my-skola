#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <poll.h>
#include <string.h>

#define MAX_LENGTH 3
#define BUFFER_SIZE 1024
#define MAX_NUMBER 666

int fill_data(int* data) {
    int length = rand() % MAX_LENGTH;
    int sum = 0;

    for (int i = 0; i < length; i++) {
        data[i] = rand() % MAX_NUMBER;
        sum += data[i];
    }

    data[length] = sum;
    if ((rand() % 13) == 0) {
        data[length] = -data[length];
    }

    return length + 1;
}

int main() {
    int fd = open("randompipe", O_WRONLY);
    printf( "Pipe open %d\n", fd );

    while ( 1 ) {
        int random_data[MAX_LENGTH];
        char string_repr[BUFFER_SIZE];
        int length = fill_data(random_data);

        int pos = 0;
        for (int i = 0; i < length - 1; i++) {
            int len = sprintf(string_repr + pos, "%d ", random_data[i]);
            pos += len;
        }
        sprintf(string_repr + pos, "%d", random_data[length - 1]);

        printf("%s\n", string_repr);
        write(fd, string_repr, strlen(string_repr) + 1);
 
        usleep(1000000); // 1s
    }

    return 0;
}
