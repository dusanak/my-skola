#include "./libs/LPS25H-driver/LPS25H.h"
#include <iostream>

int main(int argc, char** argv) {
    double pressure, temperature;

    LPS25H sensor("/dev/i2c-2");
    sensor.powerUp();

    pressure = sensor.getPressure();
    std::cout << pressure << std::endl;

    return 0;
}