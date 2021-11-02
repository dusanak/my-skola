#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <semaphore.h>

sem_t* g_sem;

void* foo(void* ptr) {
    int id = (long) ptr;

    while (!feof(stdin)) {
        int i, j;

        sem_wait(g_sem);

        printf("(%d) Input two numbers.\n", id);
        scanf("%d", &i);
        scanf("%d", &j);
        printf("(%d) Mam dve cisla %d %d.\n", id, i, j);

        sem_post(g_sem);

        usleep(100000);
    }

    return nullptr;
}

int main() {
    g_sem = sem_open("/semafor", O_CREAT | O_RDWR, 0640, 1);
    sem_init(g_sem, 1, 1);
    
    fork();
    foo((void*)(long)getpid());

    while(1) sleep(1);

    return 0;
}