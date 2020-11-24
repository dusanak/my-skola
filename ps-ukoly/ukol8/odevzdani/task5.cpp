#include <iostream>
#include <iomanip>
#include <unistd.h> 
#include <sys/wait.h>
#include <openssl/evp.h>
#include <math.h>

#define DEFAULT_STRING_LENGTH 2
#define DEFAULT_NUMBER_OF_PROCESSES 32
#define ALPHABET_SIZE 26

std::string numberToString(int input_number);
void incrementString(std::string & input_string);
std::string convertStringToMD5(std::string input_string);
void convertToMD5Parallel();

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
    
    convertToMD5Parallel();

    return 0;
}

//prevod retezce na vstupu do MD5 s vyuzitim OpenSSL
std::string convertStringToMD5(std::string input_string) {
    unsigned char md_value[EVP_MAX_MD_SIZE];
    unsigned int md_length;

    EVP_MD_CTX * mdctx = EVP_MD_CTX_create();
    EVP_DigestInit(mdctx, EVP_md5());
    EVP_DigestUpdate(mdctx, input_string.c_str(), input_string.length());
    EVP_DigestFinal(mdctx, md_value, &md_length);
    EVP_MD_CTX_destroy(mdctx);

    //prevod z unsigned char * na std::string
    std::stringstream ss;
    ss << std::hex << std::setfill('0');
    for (size_t i = 0; i < md_length; i++) {
        ss << std::setw(2) << (unsigned int)md_value[i];
    }

    return ss.str();
}

//prevede retezec na dalsi retezec v poradi
void incrementString(std::string & input_string) {
    for (int i = STRING_LENGTH - 1; i >= 0; i--) {
        if (++input_string[i] > int('z')) {
            input_string[i] = 'a';
        } else {
            return;
        }
    }
}

//prevede cislo na ekvivalentni retezec
std::string numberToString(int input_number) {
    std::string output_string = std::string(STRING_LENGTH, ' ');
    for (int i = STRING_LENGTH - 1; i >= 0; i--) {
        output_string[i] = 'a' + (input_number % ALPHABET_SIZE);
        input_number = input_number / ALPHABET_SIZE;
    }
    return output_string;
}

void convertToMD5Parallel() {
    size_t size_of_block = int(pow(ALPHABET_SIZE, STRING_LENGTH) + 0.5) / NUMBER_OF_PROCESSES;

    for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
        int pid = fork();
        if (pid == 0) {
            std::string current_string = numberToString(i * size_of_block);
        
            //posledni blok prace je vetsi aby byly zpracovany vsechny retezce
            if (i == NUMBER_OF_PROCESSES - 1) {
                size_of_block += int(pow(ALPHABET_SIZE, STRING_LENGTH) + 0.5) % size_of_block;
            }

            for (size_t j = 0; j < size_of_block; j++) {
                std::cout << convertStringToMD5(current_string) << " ";
                incrementString(current_string);
            }
            std::cout << std::endl;

            exit(0);
        }
    }

    while (waitpid(-1, NULL, 0) > 0) {}
}