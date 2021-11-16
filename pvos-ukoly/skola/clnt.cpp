#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>          /* See NOTES */
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/ip.h> /* superset of previous */
#include <errno.h>
#include <arpa/inet.h>
#include <netdb.h>

int main(int argc, char const *argv[]) {
    addrinfo *radr;
    if (argc == 2) {
        getaddrinfo(argv[1], nullptr, nullptr, &radr);
    }

    while (radr) {
        printf("AF %d ST %d PR %d ", 
               radr->ai_family, 
               radr->ai_socktype,
               radr->ai_protocol);
        radr = radr -> ai_next;

        if (radr->ai_family == AF_INET) {
            char buf[128];
            sockaddr_in* sain = (sockaddr_in *) radr->ai_addr;
            printf("ADR %s\n", inet_ntop(sain->sin_family, &sain->sin_addr, buf, strlen(buf)));
        }

        if (radr->ai_family == AF_INET6) {
            char buf[128];
            sockaddr_in6* sain = (sockaddr_in6 *) radr->ai_addr;
            printf("ADR %s\n", inet_ntop(sain->sin6_family, &sain->sin6_addr, buf, strlen(buf)));
        }

    }

    return 0;
    
    int sckt = socket(AF_INET, SOCK_STREAM, 0);
    printf("Socket: %d\n", sckt);

    sockaddr_in sain;
    sain.sin_family = AF_INET;
    sain.sin_port = htons(8888);
    int addr;
    addr = inet_pton(AF_INET, "localhost", &addr);
    sain.sin_addr.s_addr = addr;

    int ret = connect(sckt, (sockaddr *) &sain, sizeof(sain));
    if (ret < 0) {
        printf("Connect error: %d\n", errno);
    }
    
    char buf[128] = "All clear!\n";

    int len = write(sckt, buf, strlen(buf));
    if (len < 0) {
        printf("Write error: %d\n", errno);
    }
    
    close(sckt);

    return 0;
}