#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <pthread.h>
#include <fcntl.h>           /* For O_* constants */
#include <sys/stat.h>        /* For mode constants */
#include <semaphore.h>


sem_t *g_sem;

void *cteni( void *ptr )
{
    int id = ( long ) ptr; 
    while ( !feof( stdin ) )
    {
        int i, j;
        sem_wait( g_sem );
        printf( "(%d) Zadaje dve cisla:\n", id );
        scanf( "%d", &i );
        scanf( "%d", &j );
        printf( "(%d) Mam dve cisla: %d %d\n", id, i, j );
        sem_post( g_sem );
    }

    return nullptr;
}


int main()
{
    g_sem = sem_open( "/semaforek", O_RDWR | O_CREAT, 0640, 1 );
    //sem_init( g_sem, 1, 1 );

    fork();
    cteni( ( void * ) ( long ) getpid() );

    while ( 1 ) sleep( 1 );
}

