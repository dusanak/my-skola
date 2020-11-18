CC=nvcc
CFLAGS= -std=c++14 -Xcompiler -fPIC

task: task7.o cuda.o
	$(CC) $(CFLAGS) $^ -o $@

task7.o: task7.cpp
	$(CC) $(CFLAGS) -c $^ -o $@

cuda.o: cuda.cu
	$(CC) $(CFLAGS) -c $^ -o $@

clean:
	rm *.o