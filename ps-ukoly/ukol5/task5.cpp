#include <iostream>
#include <iomanip>
#include <vector>
#include <unistd.h> 
#include <sys/wait.h>
#include <openssl/evp.h>
#include <math.h>

#define DEFAULT_STRING_LENGTH 2
#define DEFAULT_NUMBER_OF_PROCESSES 32
#define ALPHABET_SIZE 26

std::string numberToString(int input_number);
void incrementString(std::string & input_string, int amount);
char incrementLetter(char input_char, int amount);
char base26toASCII(unsigned int input_number);
int asciiToBase26(char input_char);
std::string convertStringToMD5(std::string input_string);
void convertToMD5Parallel();
void convertToMD5ParallelAlt();

int STRING_LENGTH, NUMBER_OF_PROCESSES;

int main(int argc, char *argv[]) {
    if (argc == 3) {
        STRING_LENGTH = std::atoi(argv[1]);
        NUMBER_OF_PROCESSES = std::atoi(argv[2]);
    } else {
        STRING_LENGTH = DEFAULT_STRING_LENGTH;
        NUMBER_OF_PROCESSES = DEFAULT_NUMBER_OF_PROCESSES;
    }

    std::cout << "String length: " << STRING_LENGTH << "\n" << "Number of processes: " << NUMBER_OF_PROCESSES << "\n" << std::endl;
    
    convertToMD5ParallelAlt();

    return 0;
}

//Pracujeme s abecedou a-z
char base26toASCII(unsigned int input_number) {
    return char(int('a') + input_number);
}

int asciiToBase26(char input_char) {
    return int(input_char) - int('a');
}

std::string convertStringToMD5(std::string input_string) {
    unsigned char md_value[EVP_MAX_MD_SIZE];
    unsigned int md_length;

    EVP_MD_CTX * mdctx = EVP_MD_CTX_create();
    EVP_DigestInit(mdctx, EVP_md5());
    EVP_DigestUpdate(mdctx, input_string.c_str(), input_string.length());
    EVP_DigestFinal(mdctx, md_value, &md_length);
    EVP_MD_CTX_destroy(mdctx);

    //převod z unsigned char * na std::string
    std::stringstream ss;
    ss << std::hex << std::setfill('0');
    for (size_t i = 0; i < md_length; i++) {
        ss << std::setw(2) << (unsigned int)md_value[i];
    }

    return ss.str();
}

void incrementString(std::string & input_string, int amount) {
    if (input_string.length() == 0) {
        return;
    }

    for (int i = input_string.length() - 1; i >= 0; i--){
        if (amount == 0) {
            return;
        }

        char original_letter = input_string[i];
        input_string[i] = incrementLetter(original_letter, amount % ALPHABET_SIZE);
        amount = (amount + asciiToBase26(original_letter)) / ALPHABET_SIZE;
    }
}

char incrementLetter(char input_char, int amount) {
    return base26toASCII((asciiToBase26(input_char) + amount) % ALPHABET_SIZE);
}


void convertToMD5Parallel() {
    size_t size_of_block = int(pow(ALPHABET_SIZE, STRING_LENGTH) + 0.5) / NUMBER_OF_PROCESSES;

    for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
        int pid = fork();
        if (pid == 0) {
            std::string current_string = std::string(STRING_LENGTH, 'a');
            incrementString(current_string, i * size_of_block);

            if (i == NUMBER_OF_PROCESSES - 1) {
                size_of_block += int(pow(ALPHABET_SIZE, STRING_LENGTH) + 0.5) % size_of_block;
            }

            for (size_t j = 0; j < size_of_block; j++) {
                std::cout << current_string << ":" << convertStringToMD5(current_string) << " ";
                incrementString(current_string, 1);
            }
            std::cout << std::endl;

            exit(0);
        }
    }

    while (waitpid(-1, NULL, 0) > 0) {}
}

//Alternative
std::string numberToString(int input_number) {
    std::string output_string = std::string(STRING_LENGTH, ' ');
    for (int i = STRING_LENGTH - 1; i >= 0; i--) {
        output_string[i] = base26toASCII(input_number % int(pow(ALPHABET_SIZE, STRING_LENGTH - i + 1) + 0.5));
        input_number = input_number % ALPHABET_SIZE;
    }
    return output_string;
}

//Alternative
void convertToMD5ParallelAlt() {
    size_t size_of_block = int(pow(ALPHABET_SIZE, STRING_LENGTH) + 0.5) / NUMBER_OF_PROCESSES;

    int first_string = 0;

    for (int i = 0; i < STRING_LENGTH; i++) {
        first_string += int(pow(ALPHABET_SIZE, i) + 0.5);
    }

    for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
        int pid = fork();
        if (pid == 0) {
            int current_string = first_string + size_of_block * i;

            if (i == NUMBER_OF_PROCESSES - 1) {
                size_of_block += int(pow(ALPHABET_SIZE, STRING_LENGTH) + 0.5) % size_of_block;
            }

            for (size_t j = 0; j < size_of_block; j++) {
                std::cout << numberToString(current_string) << ":" << convertStringToMD5(numberToString(current_string)) << " ";
            }
            std::cout << std::endl;

            exit(0);
        }
    }

    while (waitpid(-1, NULL, 0) > 0) {}
}