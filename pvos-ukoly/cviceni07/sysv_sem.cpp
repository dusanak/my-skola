#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <fcntl.h>
#include <fcntl.h>           /* For O_* constants */
#include <sys/stat.h>        /* For mode constants */
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/sem.h>
#include <vector>

#define BUF_SIZE 4
#define NUM_OF_PRODUCERS 4

int space_sem;
int full_sem;
int access_sem;

sembuf up = { 0, 1, 0 };
sembuf down = { 0, -1, 0 };

std::vector<int> palette;
int counter = 0;

void* producer(void*) {
    while (1) {
        semop( space_sem, &down, 1 );
        semop( access_sem, &down, 1 );
        palette.push_back(counter++);
        if (palette.size() == BUF_SIZE) {
            semop( full_sem, &up, 1 );
        }
        semop( access_sem, &up, 1 );
    }
    
    return nullptr;
}

void* consumer(void*) {
    while (1) {
        semop( full_sem, &down, 1 );
        semop( access_sem, &down, 1 );
        printf("Palette is full: ");
        for (size_t i = 0; i < palette.size(); i++) {
            printf("%d ", palette[i]);
        }
        printf("\n");
        palette.clear();
        semop( access_sem, &up, 1 );
        sleep(1);

        for (int i = 0; i < BUF_SIZE; i++) {
            semop( space_sem, &up, 1 );
        }
    }

    return nullptr;    
}

int main() {
    pthread_t thread_arr[NUM_OF_PRODUCERS];
    space_sem = semget( 0x601, 1, 0600 | IPC_CREAT );
    full_sem = semget( 0x602, 1, 0600 | IPC_CREAT );
    access_sem = semget( 0x603, 1, 0600 | IPC_CREAT );

    semctl( space_sem, 0, SETVAL, BUF_SIZE );
    semctl( full_sem, 0, SETVAL, 0 );
    semctl( access_sem, 0, SETVAL, 1 );

    for (int i = 0; i < NUM_OF_PRODUCERS; i++) {
        pthread_create(thread_arr + i, nullptr, producer, nullptr);
    }

    consumer(nullptr);

    return 0;
}