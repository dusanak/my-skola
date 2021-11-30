#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>          /* See NOTES */
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/ip.h> /* superset of previous */
#include <arpa/inet.h>
#include <sys/un.h>
#include <netdb.h>

int main( int numa, char **arg )
{
    sockaddr_un saun;
    saun.sun_family = AF_UNIX;
    strcpy(saun.sun_path, "/tmp/socka");

    int skt = socket( AF_UNIX, SOCK_STREAM, 0 );
    printf( "socket %d\n", skt );

    connect( skt, ( sockaddr * ) &saun, sizeof( saun ) );

    char buf[ 128 ] = "Halo halo";
    write( skt, buf, strlen( buf ) );

    close( skt );



}
