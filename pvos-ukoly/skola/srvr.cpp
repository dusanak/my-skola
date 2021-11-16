#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>          /* See NOTES */
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/ip.h> /* superset of previous */
#include <errno.h>
#include <arpa/inet.h>

int main() {
    int sckt = socket(AF_INET, SOCK_STREAM, 0);
    printf("Socket: %d\n", sckt);

    sockaddr_in sain;
    sain.sin_family = AF_INET;
    sain.sin_port = htons(8888);
    sain.sin_addr.s_addr = INADDR_ANY;

    int ret = bind(sckt, (sockaddr *)&sain, sizeof(sain));
    if (ret < 0) {
        printf("Bind error: %d\n", errno);
        close(sckt);
    }

    ret = listen(sckt, 1);
    if (ret < 0) {
        printf("Listen error: %d\n", errno);
    }

    socklen_t sain_len = sizeof(sain);
    int cl = accept(sckt, (sockaddr *)&sain, &sain_len);
    
    char buf[128];
    printf("Client: %d %s:%d\n", 
           cl, 
           inet_ntop(sain.sin_family, &sain.sin_addr, buf, sizeof(buf)),
           ntohs(sain.sin_port));

    int len = read(cl, buf, sizeof(buf));

    write(1, buf, len);
    
    close(cl);
    close(sckt);

    return 0;
}