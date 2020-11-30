#include "./libs/HTS221-driver/HTS221.h"
#include <iostream>

int main(int argc, char** argv) {
    double humidity, temperature;

    HTS221 sensor("/dev/i2c-2");
    sensor.powerUp();
    sensor.calibrate();

    temperature = sensor.getTemperature();
    std::cout << temperature << std::endl;
    
    return 0;
}