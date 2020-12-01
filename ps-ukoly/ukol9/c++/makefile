CC       := g++
CFLAGS   := -std=c++17
CLIB     := -li2c
CSOURCES := src

TARGETS  := HTShumi HTStemp LPSpres LPStemp

all: $(TARGETS)

HTShumi: $(CSOURCES)/HTShumi.cpp HTS221.o
	$(CC) $(CFLAGS) $^ -o $@ $(CLIB)

HTStemp: $(CSOURCES)/HTStemp.cpp HTS221.o
	$(CC) $(CFLAGS) $^ -o $@ $(CLIB)

LPSpres: $(CSOURCES)/LPSpres.cpp LPS25H.o
	$(CC) $(CFLAGS) $^ -o $@ $(CLIB)

LPStemp: $(CSOURCES)/LPStemp.cpp LPS25H.o
	$(CC) $(CFLAGS) $^ -o $@ $(CLIB)

HTS221.o: $(CSOURCES)/libs/HTS221-driver/HTS221.cpp
	$(CC) $(CFLAGS) -c $^ -o $@

LPS25H.o: $(CSOURCES)/libs/LPS25H-driver/LPS25H.cpp
	$(CC) $(CFLAGS) -c $^ -o $@

clean:
	rm *.o