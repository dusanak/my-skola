#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int main(int argc, char *argv[]) {    
    int pid = fork();

    if (pid < 1) {
        exit(0);
    }

    if (pid == 0) {
        printf("I'm the child!\n");
    } else {
        printf("I'm the parent!\n");
        getchar();
        int l_pid = wait(nullptr);
        printf("Waited for child %d.\n", l_pid);
    }
    return 0;
}
