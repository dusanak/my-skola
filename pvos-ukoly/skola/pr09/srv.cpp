#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>          /* See NOTES */
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/ip.h> /* superset of previous */
#include <arpa/inet.h>

int main()
{

    int skt = socket( AF_INET, SOCK_STREAM, 0 );
    printf( "socket %d\n", skt );

    sockaddr_in sain;
    sain.sin_family = AF_INET;
    sain.sin_port = htons( 8888 );
    sain.sin_addr.s_addr = INADDR_ANY;

    int ret = bind( skt, ( sockaddr * ) &sain, sizeof( sain ) );
    printf( "bind %d\n", ret );

    int opt = 1;
    setsockopt( skt, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof( opt ) );

    ret = listen( skt, 1 );
    printf( "listen %d\n", ret );

    socklen_t sain_len = sizeof( sain );
    int cl = accept( skt, ( sockaddr * ) &sain, &sain_len );

    char buf[ 128 ];
    printf( "client %d: %s:%d\n", cl, 
            inet_ntop( sain.sin_family, &sain.sin_addr, buf, sizeof( buf ) ), 
                ntohs( sain.sin_port ) );

    int len = read( cl, buf, sizeof( buf ) );

    write( 1, buf, len );

    close( cl );
    close( skt );



}
