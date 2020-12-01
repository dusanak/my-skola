# Ukol 9
This program uses a **Sense Hat** addon to a **Raspberry PI** to track temperature, humidity and pressure. The data from these sensors is then shown using line charts on a local webpage.

## Build Info

Compile the entire program using the provided Makefiles. Afterwards, put the compiled binaries into a single directory along with the data directory. 

### C++

Run `makefile` in the directory `./c++/` using the command `make` to compile the C++ part of the program.

The C++ code is compiled using the **g++** compiler to the C++17 standard. In its current state, it should be compiled on the target device.

The program uses two drivers to connect to the **Raspberry PI** sensors.
https://github.com/jbroutier/LPS25H-driver  
https://github.com/jbroutier/HTS221-driver

### Go

Run `makefile` in the directory `./go/` using the command `make` to compile the Go part of the program.

The Go code is compiled using `env GOOS=linux GOARCH=arm GOARM=7 go build` to run on university's **ARM** computers. It can be compiled anywhere, Go compiler supports out-of-the-box cross-compilation.

The program uses a library for charts called **go-echarts**. It's a Go implementation of the **Apache ECharts** visualisation library.  
https://github.com/go-echarts/go-echarts

## How to run

Just run the executable `./bin/main` on the target device. The charts will be accessible using a web browser on the port `:8081`.