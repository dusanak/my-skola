#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <semaphore.h>
#include <vector>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <errno.h>
#include <queue>

#define BUF_SIZE 4
#define NUM_OF_PRODUCERS 4

template<class T>
class CustomQueue {
    sem_t sem;
    std::queue<T> my_queue;
    size_t my_size;

    CustomQueue(size_t size) {
        sem_init(&sem, 0, 1);
        my_size = size;
    }

    bool enqueue(T val) {
        sem_wait(&sem);
        if (my_queue.size < my_size) {
            my_queue.push(val);
            sem_post(&sem);
            return true;
        } else {
            sem_post(&sem);
            return false;
        }
    }

    T dequeue() {
        sem_wait(&sem);
        T result = my_queue.pop();
        sem_post(&sem);
        return result;
    }

    bool isEmpty() {
        return my_queue.empty();
    }

    void clear() {
        sem_wait(&sem);
        while (!isEmpty()) {
            my_queue.pop();
        }
        sem_post(&sem);
    }
};

void* producer(void*) {   
    return nullptr;
}

void* consumer(void*) {

    while (1) {
        while (1) {
            continue;
        }
    }

    return nullptr;    
}

int main() {
    pthread_t thread_arr[NUM_OF_PRODUCERS];
    
    return 0;
}