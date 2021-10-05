#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int main()
{
    while(1) {
        printf("."); // bez flushe se nevypise, \n dela flush
        fflush(stdout);
        usleep(1000);
    }

    return 0;
}
