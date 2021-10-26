#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <poll.h>

int main() {
    int fd = open("named_pipe01", O_RDONLY | O_NONBLOCK);

    printf("Pipe open %d\n", fd);

    fcntl(fd, F_SETFL, fcntl(fd, F_GETFL) & ~O_NONBLOCK);

    while (1) {
        char buf[128];
        pollfd pfd = { fd, POLLIN, 0 };
        poll(&pfd, 1, -1);

        int len = read(fd, buf, sizeof(buf));
        if (len == 0) {
            usleep(100000); // 100ms
            continue;
        }
        write(1, buf, len);
    }

    return 0;
}