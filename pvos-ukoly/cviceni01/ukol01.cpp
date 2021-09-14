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
        } else if (WIFSIGNALED(status)) {
            if (WCOREDUMP(status)) {
                printf("Process with pid %d was terminated by signal %d and produced a core dump.\n", pid, WTERMSIG(status));
            } else {
                printf("Process with pid %d was terminated by signal %d.\n", pid, WTERMSIG(status));
            }
        } else if (WIFSTOPPED(status)) {
            printf("Process with pid %d was stopped by signal %d.\n", pid, WSTOPSIG(status));
        }
    }    

    return 0;
}

int childProcess(int number) {
    if ((number % 3) == 0) {
        return number;
    } else if ((number % 7) == 0) {
        return number / 0;
    } else if ((number % 11) == 0) {
        getc(nullptr);
        return 1;
    } else {
        return 0;
    }
}