#include <iostream>
#include <vector>
#include <cstdlib>
#include <unistd.h> 
#include <sys/wait.h>

int create_twenty_children();
void parallel_bubblesort(std::vector<int> & data);
bool check_and_swap(std::vector<int> & data, int index);

int main() {
    //Ukol1

    if (create_twenty_children() == 1) {
        return 2;
    }

    //Ukol2
    std::vector<int> data = std::vector<int>();

    for (int i = 0; i < 50; i++) {
        data.push_back(std::rand() % 100);
    }

    for (int i = 0; i < 50; i++) {
        std::cout << data[i] << " ";
    }
    std::cout << std::endl;

    parallel_bubblesort(data);

    for (size_t i = 0; i < 50; i++) {
        std::cout << data[i] << " ";
    }
    std::cout << std::endl;

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
            return 1;
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
        std::cout << WEXITSTATUS(i) << ", ";
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
//TODO
void parallel_bubblesort(std::vector<int> & data) {
    bool flag = true;

    while (flag) {
        flag = false;

        for (size_t i = 0; i + 1 < data.size(); i += 2) {
            if (data[i] > data[i + 1]) {
                int tmp = data[i];
                data[i] = data[i + 1];
                data[i + 1] = tmp;
                flag = true;
            }
        }
    }
}

bool check_and_swap(std::vector<int> & data, int index) {
    if (data[index] > data[index + 1]) {
        int tmp = data[index];
        data[index] = data[index + 1];
        data[index + 1] = tmp;
        return true;
    }
    return false;
}