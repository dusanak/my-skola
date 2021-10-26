#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>

int *citac = nullptr;

int main()
{

    //int fd = open( "mapsoubor.dat", O_RDWR );
    int fd = shm_open( "/mujsoubor.dat", O_RDWR | O_CREAT, 0600 );
    printf( "soubor %d\n", fd );
    ftruncate( fd, 1024 ); 
    int len = lseek( fd, 0, SEEK_END );
    citac = ( int * ) 
        mmap( nullptr, len, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0 );

    const char *str = "Ja jsem sdilena pamet.\n";
    strcpy( ( char * ) citac, str );

    close( fd );
    fork();
    write( 1, citac, strlen( str ) );

    return 0;

    while( 1 )
    {
        printf( "(%d) citac je %d\n", getpid(), (*citac)++ );
        usleep( 10000 );
    }
}
