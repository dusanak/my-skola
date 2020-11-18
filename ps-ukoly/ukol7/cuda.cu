#include <cuda.h>
#include <cuda_runtime.h>

#include "md5.cu"

#define ALPHABET_SIZE 26

//prevede cislo na ekvivalentni retezec
__device__ void numberToString(uint64_t input_number, uint8_t ** output_string, int string_length) {   
    uint8_t tmp[40];
    tmp[string_length] = '\0';
    for (int i = string_length - 1; i >= 0; i--) {
        tmp[i] = 'a' + (input_number % ALPHABET_SIZE);
        input_number = input_number / ALPHABET_SIZE;
    }

    *output_string = tmp;
}

//prevod retezce na vstupu do MD5
__device__ void convertStringToMD5(uint64_t idx, int string_length) {
    uint8_t md_value[16];
    uint8_t * input_string;

    numberToString(idx, &input_string, string_length);
    md5(reinterpret_cast<const uint8_t *>(input_string), string_length, md_value);
    
    printf("%2.2x%2.2x%2.2x%2.2x%2.2x%2.2x%2.2x%2.2x%2.2x%2.2x%2.2x%2.2x%2.2x%2.2x%2.2x%2.2x ",
     md_value[0], md_value[1], md_value[2], md_value[3], md_value[4], md_value[5], 
     md_value[6], md_value[7], md_value[8], md_value[9], md_value[10], md_value[11], 
     md_value[12], md_value[13], md_value[14], md_value[15]);
}

__global__ void convertToMD5(uint64_t number_of_strings, int string_length) {
    //vypocitava ciselne hodnoty jednotlivych retezcu ktere jsou nasledne prepocitavany na samotne retezce
    uint64_t idx = (blockIdx.x * blockDim.x + (threadIdx.x));

    if (idx >= number_of_strings)
        return;

    convertStringToMD5(idx, string_length);
}

//generuje MD5 hashe vsech retezcu v abecede a-z pro zadanou string_length
//kazde vlakno zpracova prave jeden retezec
void generateMD5Cuda(int string_length, int number_of_threads) {
	cudaError_t cerr;

    uint64_t number_of_strings = (uint64_t)(pow((double)ALPHABET_SIZE, (double)string_length) + 0.5);

    convertToMD5<<< (number_of_strings / number_of_threads) + 1, number_of_threads >>>(number_of_strings, string_length);

	if ( ( cerr = cudaGetLastError() ) != cudaSuccess )
		printf( "CUDA Error [%d] - '%s'\n", __LINE__, cudaGetErrorString( cerr ) );

	// Output from printf is in GPU memory. 
	// To get its contens it is necessary to synchronize device.

	cudaDeviceSynchronize();
}