CC=g++
CFLAGS= -std=c++17
CLIB= -li2c

all: HTShumi HTStemp LPSpres LPStemp

HTShumi: HTShumi.cpp HTS221.o
	$(CC) $(CFLAGS) $^ -o $@ $(CLIB)

HTStemp: HTStemp.cpp HTS221.o
	$(CC) $(CFLAGS) $^ -o $@ $(CLIB)

LPSpres: LPSpres.cpp LPS25H.o
	$(CC) $(CFLAGS) $^ -o $@ $(CLIB)

LPStemp: LPStemp.cpp LPS25H.o
	$(CC) $(CFLAGS) $^ -o $@ $(CLIB)

HTS221.o: ./libs/HTS221-driver/HTS221.cpp
	$(CC) $(CFLAGS) -c $^ -o $@

LPS25H.o: ./libs/LPS25H-driver/LPS25H.cpp
	$(CC) $(CFLAGS) -c $^ -o $@

clean:
	rm *.o