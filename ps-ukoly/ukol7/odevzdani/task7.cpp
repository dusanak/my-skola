#include <iostream>
#include <iomanip>
#include <unistd.h> 
#include <sys/wait.h>
#include <math.h>
#include <string.h>

#define DEFAULT_STRING_LENGTH 2
#define DEFAULT_NUMBER_OF_THREADS 32

void generateMD5Cuda(int string_length, int number_of_threads);

int main(int argc, char *argv[]) {
    int string_length, number_of_threads;

    if (argc == 3) {
        string_length = std::atoi(argv[1]);
        number_of_threads = std::atoi(argv[2]);
    } else {
        string_length = DEFAULT_STRING_LENGTH;
        number_of_threads = DEFAULT_NUMBER_OF_THREADS;
    }

    std::cout << "String length: " << string_length << "\nNumber of threads: " << number_of_threads << "\n" << std::endl;
    
    generateMD5Cuda(string_length, number_of_threads);

    return 0;
}