#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <signal.h>
#include <string.h>

void sigio(int sig) {
    printf("Signal: %d\n", sig);
    char buf[128];
    int r = read(0, buf, sizeof(buf));
    if (r > 0) {
        write(1, buf, r);
    }
}

int main() {
    struct sigaction sa;
    sa.sa_handler = sigio;
    sa.sa_flags = 0;
    sigemptyset(&sa.sa_mask);

    sigaction(SIGIO, &sa, nullptr);



    int data_pipe[2];
    pipe(data_pipe);
    int fd = data_pipe[0];

    if (fork() == 0) {
        while (1) {
            char buf[256];

            sprintf(buf, "(%d): %d\n", getpid(), rand() % 1000);
            write(data_pipe[1], buf, strlen(buf));
        
            sleep(1);
        }
    }

    

    int flg = fcntl(fd, F_GETFL);
    fcntl(fd, F_SETFL, flg | O_ASYNC);
    fcntl(fd, F_SETSIG, SIGIO);
    fcntl(fd, F_SETOWN, getpid());
    
    while (1) { sleep(1); }    

    return 0;
}