#Ukol 9
This program uses a Sense Hat addon to a Raspberry PI to track temperature, humidity and pressure.

##C++

###Build Info

Run makefile in the directory `c++` using the command `make` to compile the C++ part of the program.

The C++ is compiled using the g++ compiler to the C++17 standard. In its current state, it should be compiled on the target device.

##Go

###Build Info

Run makefile in the directory `go` using the command `make` to compile the Go part of the program.

The Go code is compiled using `env GOOS=linux GOARCH=arm GOARM=7 go build` to run on school ARM computers.
