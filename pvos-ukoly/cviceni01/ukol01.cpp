#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

#define NUMBER_OF_PROCESSES 30 

int childProcess(int number);

int main() {
    for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
        int pid = fork();
        if (pid == 0) {
            return childProcess(i);
        }
    }

    int status, pid;
    while ((pid = waitpid(-1, &status, 0)) > 0) {
        if (WIFEXITED(status)) {
            printf("Process with pid %d exited with exit status %d.\n", pid, WEXITSTATUS(status));
        } else {
            printf("Process with pid %d did not terminate normally.\n", pid);
        }
    }    

    return 0;
}

int childProcess(int number) {
    if ((number % 3) == 0) {
        return number;
    } else if ((number % 7) == 0) {
        getc(nullptr);
        return 1;
    } else {
        return 0;
    }
}