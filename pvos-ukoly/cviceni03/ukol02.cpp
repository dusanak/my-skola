#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <signal.h>

int WAIT_COUNTER = 0;
int SIGNAL_COUNTER = 0;

#define NUMBER_OF_PROCESSES 100

int childProcess();

void intercept(int sig);

int main() {
    int number_of_running_processes = 0;
    
    struct sigaction l_sa;
    l_sa.sa_handler = intercept;
    l_sa.sa_flags = 0;

    sigaction(SIGCHLD, &l_sa, nullptr);

    while (true) {
        while (number_of_running_processes < NUMBER_OF_PROCESSES) {
            number_of_running_processes += 1;
            int pid = fork();
        
            if (pid == 0) {
                return childProcess();
            }
        }
        
        int status, pid;
        while ((pid = waitpid(-1, &status, WNOHANG)) > 0) {
            number_of_running_processes--;
            WAIT_COUNTER++;

            if (WIFEXITED(status)) {
                // printf("Process with pid %d exited with exit status %d.\n", pid, WEXITSTATUS(status));
                ;
            } else {
                printf("Process with pid %d did not terminate normally.\n", pid);
            }
        }

        printf("Wait: %d, Signal: %d\n", WAIT_COUNTER, SIGNAL_COUNTER);
    }    

    return 0;
}

int childProcess() {
    usleep(10000); // 10 ms
    return 0;
}

void intercept(int sig) {
    SIGNAL_COUNTER++;
}