#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>

int *cntr = nullptr;

int main() {
    cntr = (int *) mmap(nullptr, sizeof(int), PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);

    fork();

    while(1) {
        printf("(%d) counter value is %d.\n", getpid(), (*cntr)++);
        usleep(10000); // 10 ms;
    }

    return 0;
}