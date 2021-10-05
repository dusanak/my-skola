#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <signal.h>

void intercept(int sig) {
    printf("Intercepted signal: %d\n", sig);
}

int main() {
    struct sigaction l_sa;
    l_sa.sa_handler = intercept;
    l_sa.sa_flags = 0;

    sigaction(SIGINT, &l_sa, nullptr);

    char buf[128];
    int l_ret = read(STDIN_FILENO, buf, sizeof(buf)); 
    if (l_ret < 0) {
        printf("Error: %d\n", l_ret);
    }     

    return 0;
}