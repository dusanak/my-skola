#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int main() {
    int fork_counter = 0;
    int parent_pid = getpid();    
    
    while (true) {
        int pid = fork();

        if (pid <= 0) {
            break;
        }

        fork_counter += 1;
    }

    if (getpid() != parent_pid) {
        sleep(5);
        return 0;
    }

    int status, pid;
    while ((pid = waitpid(-1, &status, 0)) > 0) {
        if (WIFEXITED(status)) {
            // printf("Process with pid %d exited with exit status %d.\n", pid, WEXITSTATUS(status));
            ;
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

    printf("The number of forks is: %d\n", fork_counter);
    
    return 0;
}
