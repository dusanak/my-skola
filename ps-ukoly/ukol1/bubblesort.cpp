#include <iostream>
#include <vector>
#include <cstdlib>

void parallel_bubblesort(std::vector<int> & data);

int main() {
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

void parallel_bubblesort(std::vector<int> & data) {
    bool flag = true;

    while (flag) {
        flag = false;
        for (size_t i = 0; i + 1 < data.size(); i++) {
            if (data[i] > data[i + 1]) {
                int tmp = data[i];
                data[i] = data[i + 1];
                data[i + 1] = tmp;
                flag = true;
            }
        }
    }
}