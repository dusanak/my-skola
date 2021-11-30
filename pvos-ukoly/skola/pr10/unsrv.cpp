#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>          /* See NOTES */
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/ip.h> /* superset of previous */
#include <arpa/inet.h>
#include <sys/un.h>


int main()
{

    int skt = socket( AF_UNIX, SOCK_STREAM, 0 );
    printf( "socket %d\n", skt );

    sockaddr_un saun;
    saun.sun_family = AF_UNIX;
    strcpy(saun.sun_path, "/tmp/socka");

    unlink(saun.sun_path);

    int ret = bind( skt, ( sockaddr * ) &saun, sizeof( saun ) );
    printf( "bind %d\n", ret );

    ret = listen( skt, 1 );
    printf( "listen %d\n", ret );

    socklen_t saun_len = sizeof( saun );
    int cl = accept( skt, ( sockaddr * ) &saun, &saun_len );

    char buf[ 128 ];
    /*printf( "client %d: %s:%d\n", cl, 
            inet_ntop( sain.sin_family, &sain.sin_addr, buf, sizeof( buf ) ), 
                ntohs( sain.sin_port ) );*/

    int len = read( cl, buf, sizeof( buf ) );

    write( 1, buf, len );

    close( cl );
    close( skt );
}
