#include <iostream>
#include <vector>
#include <cstdlib>
#include <unistd.h> 
#include <sys/wait.h>
#include <sys/shm.h>

#define DATA_SIZE 100

int create_twenty_children();
void parallel_bubblesort(int * data);
int check_and_swap(int * data, int index);

int main() {
    //Ukol1

    create_twenty_children();

    //Ukol2
    int shmid;
    int *shmp;
    int shmkey = getuid();

    shmid = shmget(shmkey, DATA_SIZE * sizeof(int), 0644|IPC_CREAT);
    shmp = (int*)shmat(shmid,NULL,0);

    for (int i = 0; i < DATA_SIZE; i++) {
        shmp[i] = std::rand() % 100;
    }

    for (int i = 0; i < DATA_SIZE; i++) {
        std::cout << shmp[i] << " ";
    }
    std::cout << std::endl;

    parallel_bubblesort(shmp);

    for (size_t i = 0; i < DATA_SIZE; i++) {
        std::cout << shmp[i] << " ";
    }
    std::cout << std::endl;

    shmctl(shmid, IPC_RMID, 0);

    return 1;
}

//Úkol 1) Vytvořte program v jazyce C/C++, který vytvoří dvacet potomků. 
//První potomek bude čekat 1s a poté se ukončí s návratovou hodnotou 2. 
//Druhý bude čekat 2s a poté se ukončí. atd. 
//Rodičovský proces po ukončení všech potomků vypíše jejich návratové kody. (2 body)
int create_twenty_children() {
    std::vector<int> children_pids = std::vector<int>();
    std::vector<int> return_values = std::vector<int>();

    for (int i = 0; i < 20; i++) {
        int pid = fork();

        if (pid == 0) {
            sleep(i + 1);
            exit(2);
        } else {
            children_pids.push_back(pid);
        }
    }

    int status;
    while (waitpid(-1, &status, 0) > 0) {
        return_values.push_back(status);
    }

    for (int i: return_values) {
        std::cout << i << " ";
    }
    std::cout << std::endl;
 
    return 0;
}

//Úkol 2) Připravte si program pro paralelní bublikové třídění. 
//Popis algoritmu najdete například zde: https://www.slideshare.net/CristianChilipirea/oets-parallel-bubble-sort . 
//Program bude fungovat tak, že si v první fázi vytvoří tolik potomků, kolik má lichých páru. 
//Každý potomek porovná svůj pár a případně čísla přehodí. 
//Poté program počká až skončí všichni potomci (waitpid) a poté spustí procesy, které budou identicky prohazovat liché páry. 
//Vše se bude opakovat tak dlouho dokud budou potomci nějaké páry přehazovat. 
//To zda potomek nějaké páry přehodil si můžete zjistit například návratovou hodnotou potomka. 
//Vše si vyzkoušejte na poli a relativně malé délce do 100 prvků. (8.bodů)
void parallel_bubblesort(int * data) {
    bool flag = true;
    int status;

    while (flag) {
        flag = false;

        for (size_t i = 0; i + 1 < DATA_SIZE; i += 2) {
            int pid = fork();
            if (pid == 0) {
                if (check_and_swap(data, i) == 1) {
                    exit(1);
                }
                exit(0);
            }
        }

        while (waitpid(-1, &status, 0) > 0) {
            if (WEXITSTATUS(status) == 1) {
                flag = 1;
            }
        }

        for (size_t i = 1; i + 1 < DATA_SIZE; i += 2) {
            int pid = fork();
            if (pid == 0) {
                if (check_and_swap(data, i) == 1) {
                    exit(1);
                }
                exit(0);
            }
        }

        while (waitpid(-1, &status, 0) > 0) {
            if (WEXITSTATUS(status) == 1) {
                flag = 1;
            }
        }
    }
}

int check_and_swap(int * data, int index) {
    if (data[index] > data[index + 1]) {
        int tmp = data[index];
        data[index] = data[index + 1];
        data[index + 1] = tmp;
        return 1;
    }
    return 0;
}