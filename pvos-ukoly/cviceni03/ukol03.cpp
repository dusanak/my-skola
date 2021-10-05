#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <signal.h>

#define NUMBER_OF_PROCESSES 5

int childProcess();

int main() {
    int child_pids[NUMBER_OF_PROCESSES];
    int counter = 0;

    for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
        int pid = fork();
    
        if (pid == 0) {
            return childProcess();
        }

        kill(pid, SIGSTOP);
        child_pids[i] = pid;
    }

    kill(child_pids[0], SIGCONT);
    while (true) {
        sleep(3);
        kill(child_pids[(counter++) % NUMBER_OF_PROCESSES], SIGSTOP);
        kill(child_pids[counter % NUMBER_OF_PROCESSES], SIGCONT);
    }

    return 0;
}

int childProcess() {
    srandom(getpid());

    while(1) {
        printf("Pid: %d, Random number: %ld\n", getpid(), random() % 1000);
        sleep(1); // 1 s
    }
    return 0;
}