#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>

int main(int argc, char const *argv[]) {
    int link[2];

    if (pipe(link) == -1) {
        printf("Pipe creation error.");
        return 1;
    }

    for (int i = 1; i < argc; i++) {       
        int pid = fork();
        if (pid == 0) {
            dup2(link[1], STDOUT_FILENO);
            close(link[0]);
            close(link[1]);

            execlp("ls", "ls", argv[i], "-1", nullptr);

            printf("Execlp went wrong.\n");
            exit(0);
        }
    }

    close(link[1]);

    int pid;
    int row_number = 0;
    while ((pid = waitpid(-1, nullptr, 0)) > 0) { 
        sleep(1);

        while (true) {
            char l_buff[10000];
            int nbytes = read(link[0], l_buff, sizeof(l_buff));

            if (nbytes == 0) {
                break;
            }

            char* word = strtok(l_buff, "\n");
            while (word != nullptr) {
                printf("%d: %s\n", row_number++, word);

                word = strtok(nullptr, "\n");
            }
        }
    }

    return 0;
}
