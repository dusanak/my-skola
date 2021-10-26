#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>

void fill_data(double* arr, int length) {
    double sum = 0.0;
    for (int i = 0; i < length; i++) {
        arr[i] = rand() / 7.0;
        sum += arr[i];
    }
    arr[length] = sum;
}

bool compare_data(double* arr1, double* arr2, int length) {
    for (int i = 0; i < length; i++) {
        if (arr1[i] != arr2[i]) {
            return false;
        }
    }
    return true;
}

int main(int argc, char const *argv[])
{
    if (argc != 2) {
        printf("I accept one argument.\n");
        return 1;
    }

    int length = atoi(argv[1]);
    double* random_numbers = (double*)malloc(sizeof(double) * (length + 1));
    fill_data(random_numbers, length);

    int random_file = open("random_data", O_RDWR | O_CREAT | O_TRUNC, S_IRWXU);
    //printf("Open\n");

    if (random_file == -1) {
        printf("Error opening a file.\n");
        return 2;
    }

    write(random_file, random_numbers, (length + 1) * sizeof(double));
    //printf("Written\n");

    double* file_data = (double *) mmap(nullptr, length + 1, PROT_READ, MAP_PRIVATE, random_file, 0);
    //printf("Mapped\n");

    for (int i = 0; i < length + 1; i++) {
        printf("%f - %f\n", random_numbers[i], file_data[i]);
    }

    if (compare_data(random_numbers, file_data, length + 1)) {
        printf("Data matches.\n");
    } else {
        printf("Data doesn't match.\n");
    }

    close(random_file);

    return 0;
}
