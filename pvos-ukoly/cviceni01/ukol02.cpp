#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int childProcess(int time_to_run);

int main() {
    while (true) {
        int pid, status, random_number;
        
        random_number = rand() % 20;
        pid = fork();
    
        if (pid == 0) {
            return childProcess(random_number);
        }

        sleep(1);
        
        while ((pid = waitpid(-1, &status, WNOHANG)) > 0) {
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