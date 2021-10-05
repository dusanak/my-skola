#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

void intercept(int sig);

int main() {
    char text[128];

    struct sigaction l_sa;
    l_sa.sa_handler = intercept;
    l_sa.sa_flags = 0;

    sigaction(SIGALRM, &l_sa, nullptr);

    alarm(5);

    printf("Enter text:\n");
    scanf("%s", text);

    printf("%s\n", text);

    return 0;
}

void intercept(int sig) {
    printf("Time's up!\n");
    exit(0);
}