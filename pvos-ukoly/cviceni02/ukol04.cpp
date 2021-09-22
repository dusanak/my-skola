#include <stdio.h>
#include <stdlib.h>
#include <thread>
#include <chrono>

#define MAX_NUMBER_OF_THREADS 50000

void *child_thread(void* t_ptr);

int main() {
    pthread_t thread_buffer[MAX_NUMBER_OF_THREADS];
    int thread_counter = 0;

    while (thread_counter < MAX_NUMBER_OF_THREADS) {
        if (pthread_create(thread_buffer + thread_counter, nullptr, child_thread, nullptr) != 0) {
            break;
        }

        thread_counter += 1;
    }

    for (int i = 0; i < thread_counter; i++) {
        pthread_join(thread_buffer[i], nullptr);
    }
    
    printf("The number of threads is: %d\n", thread_counter);

    return 0;
}

void *child_thread(void* t_ptr) {
    return nullptr;
}