#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/select.h>
#include <sys/param.h>
#include <errno.h>

int readline( int fd, void *buff, size_t len, int s_tout ) {
    char *tempbuff = (char*)malloc(len * sizeof(char));

	fd_set pro_cteni;
	FD_ZERO(&pro_cteni); // vynulovanie/vyprazdnenie zasobniku
	FD_SET(fd, &pro_cteni);
	
	timeval tout = {s_tout, 0}; // 0 sa vracia ked vyprsi timeout, -1 pri chybe

	size_t i = 0;
	for(; i < (len - 1); i++) {
		int ret;
		ret = select(1 , &pro_cteni, nullptr, nullptr, &tout);

        if(ret == 0) {
			printf("\n!Timeout!\n");
            free(tempbuff);
			return -ETIME;
		}
		else if (ret < 0) {
			printf("Got %d error\n", ret);
            free(tempbuff);
			return ret;
		}
		
		if (FD_ISSET( fd, &pro_cteni )) {
            ret = read(fd, tempbuff + i, 1);

            if (ret == 0) {
                tempbuff[i] = '\0'; // posledny charakter treba premazat inak to robi bordel
                break;
            }
            if (tempbuff[i] == '\0')
                break;
			if (tempbuff[i] == '\n') {
				tempbuff[i + 1] = '\0'; // posledny charakter treba premazat inak to robi bordel
				// printf("Newline accepted\n");
                i++;
                break;
            }
        }
	}

    memset(buff, 0, len);
	memcpy(buff, tempbuff, len);
    free(tempbuff);

    // printf("i: %d\n", i);
	return i;
}


int main() {
    char buff[128];
    int ret;

	while (1) {
        ret = readline(0, buff, sizeof(buff), 5); // s
        if(ret < 0 || ret == -ETIME)
            continue;
        if (ret == 0)
            break;

        write(1, buff, ret);
        // printf("\n");
        usleep(100000);
    }

	return 0;
}
