#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>

int main() {
    int pipe1[2], pipe2[2];

    pipe(pipe1);
    pipe(pipe2);

    if (fork() == 0) {
        srand(getpid());
        while (true)
        {
            char buff[128];
            sprintf(buff, "(%d), %d\n", getpid(), rand() % 100000);
            write(pipe1[1], buff, strlen(buff));
            sleep(1);
        }
        exit(0);
    }

    if (fork() == 0) {
        srand(getpid());
        while (true)
        {
            char buff[128];
            sprintf(buff, "(%d), %d\n", getpid(), rand() % 100000);
            write(pipe2[1], buff, strlen(buff));
            sleep(3);
        }
        exit(0);
    }

    int flg = fcntl(pipe1[0], F_GETFL);
    fcntl(pipe1[0], F_SETFL, flg | O_NONBLOCK);
    flg = fcntl(pipe2[0], F_GETFL);
    fcntl(pipe2[0], F_SETFL, flg | O_NONBLOCK);
    
    while(true) {
        char buff[128];
        int ret;
        ret = read(pipe1[0], buff, sizeof(buff));
        if (ret > 0) { write(1, buff, ret); }
        ret = read(pipe2[0], buff, sizeof(buff));
        if (ret > 0) { write(1, buff, ret); }

        flg = fcntl(0, F_GETFL);
        fcntl(0, F_SETFL, flg | O_NONBLOCK);
        ret = read(0, buff, sizeof(buff));
        if (ret > 0) {
            if (strncmp(buff, "quit", 4) == 0) return 0;
        }

        usleep(10000); // 10ms
    }

    return 0;
}