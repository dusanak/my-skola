#include <iostream>
#include <iomanip>
#include <unistd.h> 
#include <sys/wait.h>
#include <math.h>
#include <string.h>

#define DEFAULT_STRING_LENGTH 2

void generateMD5Cuda(int string_length);

int main(int argc, char *argv[]) {
    int string_length, number_of_processes;

    if (argc == 2) {
        string_length = std::atoi(argv[1]);
    } else {
        string_length = DEFAULT_STRING_LENGTH;
    }

    std::cout << "String length: " << string_length << "\n" << std::endl;
    
    generateMD5Cuda(string_length);

    return 0;
}