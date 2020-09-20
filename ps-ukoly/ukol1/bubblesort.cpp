#include <iostream>
#include <vector>
#include <cstdlib>

void bubblesort(std::vector<int> & data);

int main() {
    std::vector<int> data = std::vector<int>();

    for (int i = 0; i < 20000; i++) {
        data.push_back(std::rand() % 10000);
    }

    for (int i = 0; i < 50; i++) {
        std::cout << data[i] << " ";
    }

    std::cout << std::endl;

    bubblesort(data);

    for (int i = 0; i < 50; i++) {
        std::cout << data[i] << " ";
    }

    std::cout << std::endl;

    return 1;
}

void bubblesort(std::vector<int> & data) {
    bool flag = true;

    while (flag) {
        flag = false;
        for (int i = 0; i < data.size(); i++) {
            if ((i + 1 < data.size()) && (data[i] > data[i + 1])) {
                int tmp = data[i];
                data[i] = data[i + 1];
                data[i + 1] = tmp;
                flag = true;
            }
        }
    }
}