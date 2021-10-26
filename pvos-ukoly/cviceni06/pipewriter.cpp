#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <poll.h>

#define MAX_LENGTH 5
#define MAX_NUMBER 666

int fill_data(int* data) {
    int length = rand() % (MAX_LENGTH - 1);
    int sum = 0;

    for (int i = 0; i < length; i++) {
        data[i] = rand() % MAX_NUMBER;
        sum += data[i];
    }

    data[length] = sum;
    if ((rand() % 13) == 0) {
        data[length + 1] = -data[length + 1];
    }

    return length + 1;
}

int num_arr_length_w_spaces(int* num_arr, int length) {
    length = 0;
    for (int i = 0; i < length; i++) {
        if (num_arr[i] < 0) {
            length += 1;
        }

        length += (num_arr[i] / 10) + 1; 

        length += 1;
    }

    return length;
}

int main() {
    int fd = open("randompipe", O_WRONLY);
    printf( "Pipe open %d\n", fd );

    while ( 1 ) {
        int random_data[MAX_LENGTH];
        int length = fill_data(random_data);

        int str_length = num_arr_length_w_spaces(random_data, length);
        char* string_repr = (char *)malloc(str_length * sizeof(char));

        int pos = 0;
        for (int i = 0; i < length; i++) {
            int len = sprintf(string_repr + pos, "%d ", random_data[i]);
            pos += len;
        }
        //string_repr[pos] = '\n';

        printf("%s\n", string_repr);
        write(fd, string_repr, str_length);

        free(string_repr); 
        usleep(1000000); // 1s
    }

    return 0;
}
