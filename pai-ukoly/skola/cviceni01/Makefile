##############################################################################
#
# Simple Makefile example
#
# This Makefile will initiate compilation of all *.cpp files 
# in current directory into individual executables. 
#
##############################################################################

# Sources *.cpp can be changed to list of individual files

CPPFLAGS = clang++ -std=c++17 -Wall -pthread

.PHONY: all clean 

all: cSocket.o cClientSocket.o task.out

cSocket.o: cSocket.cpp cSocket.hpp 
	$(CPPFLAGS) -c cSocket.cpp

cClientSocket.o: cClientSocket.cpp cClientSocket.hpp
	$(CPPFLAGS) -c cClientSocket.cpp 

task.out: cSocket.o cClientSocket.o task.cpp
	$(CPPFLAGS) cSocket.o cClientSocket.o task.cpp -o task.out 

clean:
	rm -rf *.o *.gch *.out

