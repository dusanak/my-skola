#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main() {
    char znaky[] = {'-', '\\', '|', '/'};
    int counter = 0;
    while (1) {
        printf("%c\r", znaky[(counter++) % 4]);
        fflush(stdout);
        usleep(500000); // 500 ms
    }

    return 0;
}