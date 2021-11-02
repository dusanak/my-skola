#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <semaphore.h>
#include <vector>

#define BUF_SIZE 4
#define NUM_OF_PRODUCERS 1

int space_sem[2];
int full_sem[2];
int access_sem[2];
unsigned char msg = 1;
unsigned char ret;
std::vector<int> palette;
int counter = 0;

void* producer(void*) {
    while (1) {
        read(space_sem[0], &ret, 1);
        read(access_sem[0], &ret, 1);
        palette.push_back(counter++);
        if (palette.size() == BUF_SIZE) {
            write(full_sem[1], &msg, 1);
        }
        write(access_sem[1], &msg, 1);
    }
    
    return nullptr;
}

void* consumer(void*) {
    while (1) {
        read(full_sem[0], &ret, 1);
        read(access_sem[0], &ret, 1);

        printf("Palette is full: ");
        for (size_t i = 0; i < palette.size(); i++) {
            printf("%d ", palette[i]);
        }
        printf("\n");
        palette.clear();
        write(access_sem[1], &msg, 1);
        sleep(1);

        for (int i = 0; i < BUF_SIZE; i++) {
            write(space_sem[1], &msg, 1);
        }
    }

    return nullptr;    
}

int main() {
    pthread_t thread_arr[NUM_OF_PRODUCERS];

    pipe(space_sem);
    for (int i = 0; i < BUF_SIZE; i++) {
        write(space_sem[1], &msg, 1);
    }
    pipe(full_sem);
    pipe(access_sem);
    write(access_sem[1], &msg, 1);

    for (int i = 0; i < NUM_OF_PRODUCERS; i++) {
        pthread_create(thread_arr + i, nullptr, producer, nullptr);
    }

    consumer(nullptr);

    return 0;
}