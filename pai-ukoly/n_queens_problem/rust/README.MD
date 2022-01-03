# N Queens Problem Solver - Rust
This program uses a calculates the number of possible placements of **N** queens on a **N**x**N** chessboard using threads for greater speed.

The method used is an iterative DFS search of all options. Possible solutions are generated row by row. If several possible queen placemenents are found for a currently processed row, these extra potential solutions are saved into a stack. Each thread has a local stack. If this stack becomes too full, extra potential solutions are moved to a shared stack from which threads that don't currently have anything to do get their work.

## Build Info

The program is compiled using Cargo, the Rust package manager. All of the below commands are called in this directory (`../rust/`).

    cargo build             - compiles the code with debug optimisations
    cargo build --release   - compiles the code with release optimisations

## How to run

Run the executable `./target/release/rust` or `./target/debug/rust`, depending on the command above that was used. 
The program can also be run using the command `cargo run` or `cargo run --release`.

The program has two optional arguments, the first one sets the number of queens for which the problem is solved. The second argument sets the number of threads to be used. Default values are 4 and 1 respectively.

**Examples:**

All of these examples are run in this directory (`../rust/`).

    ./target/release/rust         - runs the release optimised program
                                    with default values, 4 queens and 1 thread.
    ./target/debug/rust 12 4      - runs the debug optimised program for
                                    12 queens and utilising 4 threads.

    cargo run --release 14 8      - builds and runs the code using the 
                                    release optimisations for 14 queens 
                                    and utilisiing 8 threads.

