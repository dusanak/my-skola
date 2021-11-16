#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <semaphore.h>
#include <vector>
#include <fcntl.h>           /* For O_* constants */
#include <sys/stat.h>        /* For mode constants */
#include <mqueue.h>
#include <errno.h>
#include <string.h>

#define BUF_SIZE 4
#define NUM_OF_PRODUCERS 1

#define SPACE_MSG 1
#define ACCESS_MSG 2
#define FULL_MSG 3

std::vector<int> palette;
int counter = 0;

struct MyMsg 
{
    long hdr;
    int val;
};

#define MyMsgSize ( sizeof( MyMsg ) )

void* producer(void*) {
    int msgfdspace = mq_open( "/queue_space", O_CREAT | O_RDWR, 0640, nullptr );
    int msgfdaccess = mq_open( "/queue_access", O_CREAT | O_RDWR, 0640, nullptr );
    int msgfdfull = mq_open( "/queue_full", O_CREAT | O_RDWR, 0640, nullptr );

    mq_attr mqspaceatr;
    mq_getattr( msgfdspace, &mqspaceatr );
    mq_attr mqaccessatr;
    mq_getattr( msgfdaccess, &mqaccessatr );
    mq_attr mqfullatr;
    mq_getattr( msgfdfull, &mqfullatr );

    while (1) {
        MyMsg msg;
        int ret;
        
        ret = mq_receive( msgfdspace, (char *) &msg, mqspaceatr.mq_msgsize, nullptr);
        if ( ret < 0 ) {
            printf("Receive error: %s\n", strerror(errno));
            sleep(1);
            continue;
        }
        ret = mq_receive( msgfdaccess, (char *) &msg, mqaccessatr.mq_msgsize, nullptr);
        if ( ret < 0 ) {
            printf("Receive error: %d\n", errno);
            sleep(1);
            continue;
        }

        palette.push_back(counter++);
        if (palette.size() == BUF_SIZE) {
            ret = mq_send( msgfdfull, ( char * ) &msg, MyMsgSize, 0 );
            if (ret < 0) {
                printf("Send error: %d\n", errno);
                sleep(1);
                continue;
            }
        }
        ret = mq_send( msgfdaccess, ( char * ) &msg, MyMsgSize, 0 );
        if (ret < 0) {
            printf("Send error: %d\n", errno);
            sleep(1);
            continue;
        }
    }
    
    return nullptr;
}

void* consumer(void*) {
    int msgfdspace = mq_open( "/queue_space", O_CREAT | O_RDWR, 0640, nullptr );
    int msgfdaccess = mq_open( "/queue_access", O_CREAT | O_RDWR, 0640, nullptr );
    int msgfdfull = mq_open( "/queue_full", O_CREAT | O_RDWR, 0640, nullptr );

    mq_attr mqspaceatr;
    mq_getattr( msgfdspace, &mqspaceatr );
    mq_attr mqaccessatr;
    mq_getattr( msgfdaccess, &mqaccessatr );
    mq_attr mqfullatr;
    mq_getattr( msgfdfull, &mqfullatr );

    while (1) {
        MyMsg msg;
        int ret;

        ret = mq_receive( msgfdfull, (char *) &msg, mqfullatr.mq_msgsize, nullptr);
        if ( ret < 0 ) {
            printf("Receive error: %d\n", errno);
            sleep(1);
            continue;
        }
        ret = mq_receive( msgfdaccess, (char *) &msg, mqaccessatr.mq_msgsize, nullptr);
        if ( ret < 0 ) {
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

        ret = mq_send( msgfdaccess, ( char * ) &msg, MyMsgSize, 0 );
        if (ret < 0) {
            printf("Send error: %d\n", errno);
            sleep(1);
            continue;
        }
        sleep(1);

        for (int i = 0; i < BUF_SIZE; i++) {
            ret = mq_send( msgfdspace, ( char * ) &msg, MyMsgSize, 0 );
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
    
    int msgfdspace = mq_open( "/queue_space", O_CREAT | O_RDWR, 0640, nullptr );
    int msgfdaccess = mq_open( "/queue_access", O_CREAT | O_RDWR, 0640, nullptr );

    MyMsg msg;
    msg.hdr = 1;
    msg.val = 1;
    int ret;
    
    for (int i = 0; i < BUF_SIZE; i++) {
        ret = mq_send( msgfdspace, ( char * ) &msg, MyMsgSize, 0 );

        if (ret < 0) {
            printf("Send error: %d\n", errno);
            sleep(1);
            continue;
        }
    }

    ret = mq_send( msgfdaccess, ( char * ) &msg, MyMsgSize, 0 );
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