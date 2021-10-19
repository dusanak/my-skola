#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <fcntl.h>
#include <sys/select.h>
#include <sys/param.h>

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
    
    while(true) {
        char buff[128];
        int ret;

        fd_set read_only;
        FD_ZERO(&read_only);
        FD_SET(0, &read_only);
        FD_SET(pipe1[0], &read_only);
        FD_SET(pipe2[0], &read_only);
        int max = MAX (pipe1[0], pipe2[0]);

        int flg = fcntl(pipe1[0], F_GETFL);
        fcntl(pipe1[0], F_SETFL, flg | O_NONBLOCK);
        flg = fcntl(pipe2[0], F_GETFL);
        fcntl(pipe2[0], F_SETFL, flg | O_NONBLOCK);

        timeval tout = {0, 500000};
        ret = select( max + 1, &read_only, nullptr, nullptr, &tout);

        if (ret == 0) { printf("Nuda\n"); }
        else { printf("tout: %ld:%06ld\n", tout.tv_sec, tout.tv_usec); }

        if (FD_ISSET(pipe1[0], &read_only)) {
            ret = read(pipe1[0], buff, sizeof(buff));
            if (ret > 0) { write(1, buff, ret); }
        }
        
        if (FD_ISSET(pipe2[0], &read_only)) {
            ret = read(pipe2[0], buff, sizeof(buff));
            if (ret > 0) { write(1, buff, ret); }
        }

        if (FD_ISSET(0, &read_only)) {
            flg = fcntl(0, F_GETFL);
            fcntl(0, F_SETFL, flg | O_NONBLOCK);
            ret = read(0, buff, sizeof(buff));
            if (ret > 0) {
                if (strncmp(buff, "quit", 4) == 0) return 0;
            }
        }        
    }

    return 0;
}