#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <semaphore.h>
#include <vector>

#define BUF_SIZE 4
#define NUM_OF_PRODUCERS 4

sem_t space_sem;
sem_t full_sem;
sem_t access_sem;
std::vector<int> palette;
int counter = 0;

void* producer(void*) {
    while (1) {
        sem_wait(&space_sem);
        sem_wait(&access_sem);
        palette.push_back(counter++);
        if (palette.size() == BUF_SIZE) {
            sem_post(&full_sem);
        }
        sem_post(&access_sem);
    }
    
    return nullptr;
}

void* consumer(void*) {
    while (1) {
        sem_wait(&full_sem);
        sem_wait(&access_sem);
        printf("Palette is full: ");
        for (size_t i = 0; i < palette.size(); i++) {
            printf("%d ", palette[i]);
        }
        printf("\n");
        palette.clear();
        sem_post(&access_sem);
        sleep(1);

        for (int i = 0; i < BUF_SIZE; i++) {
            sem_post(&space_sem);
        }
    }

    return nullptr;    
}

int main() {
    pthread_t thread_arr[NUM_OF_PRODUCERS];
    sem_init(&space_sem, 0, BUF_SIZE);
    sem_init(&full_sem, 0, 0);
    sem_init(&access_sem, 0, 1);

    for (int i = 0; i < NUM_OF_PRODUCERS; i++) {
        pthread_create(thread_arr + i, nullptr, producer, nullptr);
    }

    consumer(nullptr);

    return 0;
}