# N Queens Problem Solver - Go
This program uses a calculates the number of possible placements of **N** queens on a **N**x**N** chessboard using threads for greater speed.

The method is a recursive DFS search of all options. Possible solutions are generated row by row. For each generated possible solution a goroutine is started which processed the next row. This solution is memory limited due to its recursive nature. On my local machine (6 hyper-threaded cores, 16GB RAM), 14 queens is the maximum that can be solved before the program causes a memory overflow.

## Build Info

The program is compiled using Go. All of the below commands are called in this directory (`../go/`).

    go build             - compiles the code

## How to run

Run the executable `./go`. 
The program can also be run using the command `go run .`

The program has two optional arguments, the first one sets the number of queens for which the problem is solved. The second argument sets whether the program should use the serial or parallel implementation. Default values are 4 and true respectively.

**Examples:**

All of these examples are run in this directory (`../go/`).

    ./go                  - runs the compiled program with default values 
                            4 queens using the parallel implementation
    
    go run . 12 true      - builds and runs the code for 12 queens
                            using the parallel implementation

    go run . 8 false      - builds and runs the code for 8 queens
                            using the serial implementation

