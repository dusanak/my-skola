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

#define BUF_SIZE 4
#define NUM_OF_PRODUCERS 4

#define SPACE_MSG 1
#define ACCESS_MSG 2
#define FULL_MSG 3

#define MyMsgSize ( sizeof( MyMsg ) )
struct MyMsg 
{
    long hdr;
    int val;
};

std::vector<int> palette;
int counter = 0;

void* producer(void*) {
    int msgid = msgget(0x420, 0600 | IPC_CREAT);

    while (1) {
        MyMsg msg;
        int ret;

        ret = msgrcv(msgid, &msg, MyMsgSize, SPACE_MSG, 0);
        if (ret < 0) {
            printf("Receive error: %d\n", errno);
            sleep(1);
            continue;
        }
        ret = msgrcv(msgid, &msg, MyMsgSize, ACCESS_MSG, 0);
        if (ret < 0) {
            printf("Receive error: %d\n", errno);
            sleep(1);
            continue;
        }

        palette.push_back(counter++);
        if (palette.size() == BUF_SIZE) {
            msg.hdr = FULL_MSG;
            int ret = msgsnd(msgid, &msg, MyMsgSize, 0);
            if (ret < 0) {
                printf("Send error: %d\n", errno);
                sleep(1);
                continue;
            }
        }
        msg.hdr = ACCESS_MSG;
        ret = msgsnd(msgid, &msg, MyMsgSize, 0);
        if (ret < 0) {
            printf("Send error: %d\n", errno);
            sleep(1);
            continue;
        }
    }
    
    return nullptr;
}

void* consumer(void*) {
    int msgid = msgget(0x420, 0600 | IPC_CREAT);

    while (1) {
        MyMsg msg;
        int ret;

        ret = msgrcv(msgid, &msg, MyMsgSize, FULL_MSG, 0);
        if (ret < 0) {
            printf("Receive error: %d\n", errno);
            sleep(1);
            continue;
        }
        ret = msgrcv(msgid, &msg, MyMsgSize, ACCESS_MSG, 0);
        if (ret < 0) {
            printf("Receive error: %d\n", errno);
            sleep(1);
            continue;
        }

        printf("Palette is full: ");
        for (size_t i = 0; i < palette.size(); i++) {
            printf("%d ", palette[i]);
        }
        printf("\n");
        palette.clear();

        msg.hdr = ACCESS_MSG;
        ret = msgsnd(msgid, &msg, MyMsgSize, 0);
        if (ret < 0) {
            printf("Send error: %d\n", errno);
            sleep(1);
            continue;
        }

        sleep(1);

        for (int i = 0; i < BUF_SIZE; i++) {
            msg.hdr = SPACE_MSG;
            int ret = msgsnd(msgid, &msg, MyMsgSize, 0);
            if (ret < 0) {
                printf("Send error: %d\n", errno);
                sleep(1);
                continue;
            }
        }
    }

    return nullptr;    
}

int main() {
    pthread_t thread_arr[NUM_OF_PRODUCERS];
    int msgid = msgget(0x420, 0600 | IPC_CREAT);
    MyMsg msg;
    int ret;

    for (int i = 0; i < BUF_SIZE; i++) {
        msg.hdr = SPACE_MSG;
        ret = msgsnd(msgid, &msg, MyMsgSize, 0);
        if (ret < 0) {
            printf("Send error: %d\n", errno);
            sleep(1);
            continue;
        }
    }

    msg.hdr = ACCESS_MSG;
    ret = msgsnd(msgid, &msg, MyMsgSize, 0);
    if (ret < 0) {
        printf("Send error: %d\n", errno);
        sleep(1);
    }

    for (int i = 0; i < NUM_OF_PRODUCERS; i++) {
        pthread_create(thread_arr + i, nullptr, producer, nullptr);
    }

    consumer(nullptr);

    return 0;
}