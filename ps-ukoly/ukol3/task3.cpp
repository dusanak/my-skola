#include <iostream>
#include <vector>
#include <unistd.h> 
#include <sys/wait.h>
//#include <openssl/evp.h>

#define STRING_LENGTH 2
#define NUMBER_OF_PROCESSES 3

//todo https://www.openssl.org/docs/man1.0.2/man3/EVP_DigestInit.html
//https://www.openssl.org/docs/man1.0.2/man3/md5.html

void generateStrings(std::vector<std::string> & data, int string_length);
std::string convertStringToMD5(std::string input_string);
void convertToMD5Sequential(std::vector<std::string> & data);
void convertToMD5Parallel(std::vector<std::string> & data);

int main() {
    //Ukol1
    std::vector<std::string> data = std::vector<std::string>();
    generateStrings(data, STRING_LENGTH);

    for (std::string i: data) {
        std::cout << i << " ";
    }
    std::cout << std::endl;

    std::cout << std::endl;
    convertToMD5Sequential(data);

    //Ukol2
    std::cout << std::endl;
    convertToMD5Parallel(data);

    return 0;
}

//Ukol1
//Vytvořte program který bude počítat md5 hash ze sady textových řetězců. 
//Váš program bude ve smyčce generovat řetězce “aaa”,“aab”, …. ,“zzz”. 
//Program koncipujte tak, aby bylo možné délku řetězce předem definovat (tedy tři znaky, čtyři znaky a podobně ). 
//Pro každý vygenerovaný řetězec vypočítejte md5 hash. 
//Funkci pro výpočet md5 hash najdete na mých stránkách, případně https://github.com/pod32g/MD5/blob/master/md5.c . 
//Zdrojů je ale více, takže nebráním se ani použití standardních knihoven, nebo jiné implementaci. (2 body)
void generateStrings(std::vector<std::string> & data, int string_length) {
    if (string_length == 0) {
        data.push_back("");
        return;
    }

    std::vector<std::string> temp = std::vector<std::string>();
    generateStrings(temp, string_length - 1);

    
    for (int i = 0; i < 26; i++) {
        for (std::string j: temp) {
            data.push_back(char('a' + i) + j); 
        }
    }
}

//TODO useMD5
std::string convertStringToMD5(std::string input_string) {
    std::string temp = "";

    for (char i: input_string) {
        temp = temp + char(i + 1);
    }

    return temp;
}

void convertToMD5Sequential(std::vector<std::string> & data) {
    for (size_t i = 0; i < data.size(); i++) {
        std::cout << convertStringToMD5(data[i]) << " ";
    }
    std::cout << std::endl;
}

//Ukol2
//Předchozí program upravte tak, aby si vstupní sadu řetězců rozdělil na N částí. 
//Přičemž každou část bude zpracovávat jeden proces. Tedy příklad. 
//Vstupní data budou řetězce “aaa”,“aab” až “zzz”. Program si vytvoří 32 procesů, 
//přičemž první proces bude počítat md5 hashe pro řetězce “aaa”,“aab” až “azz”, 
//druhý proces bude řešit “baa”,“bab” až “baz” a tak dále až poslední proces bude řešit řetězce “zaa”,“zab” až “zzz” . 
//Program opět koncipujte tak, aby bylo bylo možné jako parametr zadat délku řetězce a počet procesů, které budou v rámci běhu programu vytvořeny. (8 bodů)
void convertToMD5Parallel(std::vector<std::string> & data) {
    size_t size_of_block = data.size() / NUMBER_OF_PROCESSES;

    for (int i = 0; i < NUMBER_OF_PROCESSES; i++) {
        int pid = fork();
        if (pid == 0) {
            for (size_t j = i * size_of_block; j < (i + 1) * size_of_block; j++) {
                std::cout << convertStringToMD5(data[j]) << " ";
            }
            std::cout << std::endl;

            exit(0);
        }
    }

    while (waitpid(-1, NULL, 0) > 0) {}
}
