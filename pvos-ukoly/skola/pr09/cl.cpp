#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>          /* See NOTES */
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/ip.h> /* superset of previous */
#include <arpa/inet.h>
#include <netdb.h>

int main( int numa, char **arg )
{

    addrinfo *radr, hint;
    bzero( &hint, sizeof( hint ) );
    hint.ai_family = AF_INET;
    hint.ai_socktype = SOCK_STREAM;

    getaddrinfo( arg[ 1 ], nullptr, &hint, &radr );

    addrinfo *backup = radr;
    /*
    while ( radr ) 
    {
        printf( "AF %d ST %d PR %d ", 
                radr->ai_family,
                radr->ai_socktype,
                radr->ai_protocol );
        if ( radr->ai_family == AF_INET )
        {
            char buf[ 128 ];
            sockaddr_in *sain = ( sockaddr_in * ) radr->ai_addr;
            printf( "ADR %s\n", inet_ntop( radr->ai_family, &sain->sin_addr,
                        buf, sizeof( buf ) ) );
        }
        if ( radr->ai_family == AF_INET6 )
        {
            char buf[ 128 ];
            sockaddr_in6 *sain = ( sockaddr_in6 * ) radr->ai_addr;
            printf( "ADR %s\n", inet_ntop( radr->ai_family, &sain->sin6_addr,
                        buf, sizeof( buf ) ) );
        }
        radr = radr->ai_next;
    }
    */

    sockaddr_in sain = * ( sockaddr_in * ) backup->ai_addr;
    sain.sin_port = htons( atoi( arg[ 2 ] ) );

    freeaddrinfo( backup );

    int skt = socket( AF_INET, SOCK_STREAM, 0 );
    printf( "socket %d\n", skt );

    connect( skt, ( sockaddr * ) &sain, sizeof( sain ) );

    char buf[ 128 ] = "Halo halo";
    write( skt, buf, strlen( buf ) );

    close( skt );



}
