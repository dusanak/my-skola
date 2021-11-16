#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>

#define MyMsgSize (sizeof(MyMsg) - sizeof(long))
struct MyMsg {
    long hdr;
    union {
        double d;
        long l;
    };
};

int main() {
    char types[] = "dl";
    int msgid = msgget(0x420, 0600 | IPC_CREAT);
    printf("Message queue id: %d\n", msgid);

    while (1) {
        int choice = rand() % 2;
        MyMsg msg;

        if (choice == 0) {
            msg.hdr = types[choice];
            msg.d = (double) (rand() % 4096) / 100.0;
        } else {
            msg.hdr = types[choice];
            msg.l = rand() % 4096;
        }

        int ret = msgsnd(msgid, &msg, MyMsgSize, 0);
        printf("Message sent %c: %d. ", types[choice], ret);

        if (choice == 0) {
            printf("Value is %f.\n", msg.d);
        } else {
            printf("Value is %ld.\n", msg.l);
        }

        sleep(1);
    }

    return 0;
}