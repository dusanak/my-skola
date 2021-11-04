#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <pthread.h>
#include <fcntl.h>           /* For O_* constants */
#include <sys/stat.h>        /* For mode constants */
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/sem.h>

int sem_id = -1;

void *cteni( void *ptr )
{
    sembuf up = { 0, 1, 0 };
    sembuf down = { 0, -1, 0 };
    int id = ( long ) ptr; 
    while ( !feof( stdin ) )
    {
        int i, j;
        //sem_wait( g_sem );
        semop( sem_id, &down, 1 );
        printf( "(%d) Zadaje dve cisla:\n", id );
        scanf( "%d", &i );
        scanf( "%d", &j );
        printf( "(%d) Mam dve cisla: %d %d\n", id, i, j );
        //sem_post( g_sem );
        semop( sem_id, &up, 1 );
    }

    return nullptr;
}


int main()
{
    sem_id = semget( 0xcafe, 1, 0600 | IPC_CREAT );

    printf( "mam semafor %d\n", sem_id );

    semctl( sem_id, 0, SETVAL, 1 );

    fork();
    cteni( ( void * ) ( long ) getpid() );

    while ( 1 ) sleep( 1 );
}

