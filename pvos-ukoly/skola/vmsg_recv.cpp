#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <errno.h>

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
        MyMsg msg;

        int ret = msgrcv(msgid, &msg, MyMsgSize, 0, 0);

        if (ret < 0) {
            printf("Receive error: %d\n", errno);
            continue;
        }

        if (msg.hdr == types[0]) {
            printf("Received a message of type %c, %f.\n", types[0], msg.d);
        } else {            
            printf("Received a message of type %c, %ld.\n", types[1], msg.l);
        }
    }

    return 0;
}