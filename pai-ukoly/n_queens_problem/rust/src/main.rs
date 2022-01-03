mod parallel_n_queens;
mod serial_n_queens;

use std::env;

fn main() {
    let args: Vec<String> = env::args().collect();

    let chess_size: u32 = match args.get(1) {
        None => 4,
        Some(x) => x.parse().unwrap_or(4),
    };

    let number_of_threads: u32 = match args.get(2) {
        None => 1,
        Some(x) => x.parse().unwrap_or(1),
    };

    let is_parallel: bool = match args.get(3) {
        None => true,
        Some(x) => x.parse().unwrap_or(true),
    };

    if !is_parallel {
        println!("Serial solution for {} queens", chess_size);
        serial_n_queens::solve_serial(chess_size);
    } else {
        println!("Parallel solution for {} queens using {} threads", chess_size, number_of_threads);
        parallel_n_queens::solve_parallel(chess_size, number_of_threads);
    }
}
