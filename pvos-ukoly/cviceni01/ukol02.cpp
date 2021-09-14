#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

#define NUMBER_OF_PROCESSES 10 

int childProcess(int time_to_run);

int main() {
    int number_of_running_processes = 0;
    while (true) {
        while (number_of_running_processes < NUMBER_OF_PROCESSES) {
            int random_number = rand() % 15;

            number_of_running_processes += 1;
            int pid = fork();
        
            if (pid == 0) {
                return childProcess(random_number);
            }
        }
        
        int status, pid;
        while ((pid = waitpid(-1, &status, WNOHANG)) > 0) {
            number_of_running_processes -= 1;

            if (WIFEXITED(status)) {
                printf("Process with pid %d exited with exit status %d.\n", pid, WEXITSTATUS(status));
            } else {
                printf("Process with pid %d did not terminate normally.\n", pid);
            }
        }
    }    

    return 0;
}

int childProcess(int time_to_run) {
    sleep(time_to_run);
    return 0;
}