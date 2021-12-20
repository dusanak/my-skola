use std::collections::VecDeque;
use std::fmt;
use std::sync::Arc;
use std::sync::Mutex;
use std::thread;

#[derive(Clone)]
struct ChessBoard {
    size: u32,
    queens: Vec<(u32, u32)>,
}

impl ChessBoard {
    fn new(size: u32) -> ChessBoard {
        ChessBoard {
            size: size,
            queens: Vec::new(),
        }
    }

    fn place_queen(&mut self, position: (u32, u32)) -> bool {
        if !self.can_place_queen(position) {
            return false;
        };

        self.queens.push(position);
        true
    }

    fn can_place_queen(&self, position: (u32, u32)) -> bool {
        self.is_horizontal_free(position)
            && self.is_vertical_free(position)
            && self.is_diagonal_free(position)
    }

    fn is_horizontal_free(&self, position: (u32, u32)) -> bool {
        for queen in &self.queens {
            if queen.1 == position.1 {
                return false;
            }
        }
        true
    }

    fn is_vertical_free(&self, position: (u32, u32)) -> bool {
        for queen in &self.queens {
            if queen.0 == position.0 {
                return false;
            }
        }
        true
    }

    fn is_diagonal_free(&self, position: (u32, u32)) -> bool {
        for queen in &self.queens {
            if (queen.0 as i32 - queen.1 as i32) == (position.0 as i32 - position.1 as i32) {
                return false;
            }

            if (queen.0 as i32 + queen.1 as i32) == (position.0 as i32 + position.1 as i32) {
                return false;
            }
        }
        true
    }
}

impl fmt::Display for ChessBoard {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, " ").unwrap();
        for x in 0..self.size {
            write!(f, "{}", x).unwrap();
        }
        writeln!(f, "").unwrap();

        let mut chess_board = vec!['.'; (self.size * self.size) as usize];

        for queen in &self.queens {
            chess_board[(queen.0 + queen.1 * self.size) as usize] = 'Q';
        }

        for y in 0..self.size {
            write!(f, "{} ", y).unwrap();
            for x in 0..self.size {
                write!(f, "{}", chess_board[(x + y * self.size) as usize]).unwrap();
            }
            writeln!(f, "").unwrap();
        }

        Ok(())
    }
}

fn generate_row_variants(
    mut chess_board: ChessBoard,
    chess_boards: &Arc<Mutex<VecDeque<ChessBoard>>>,
) {
    let y = chess_board.queens.len();

    for x in 0..chess_board.size as usize {
        if !chess_board.can_place_queen((x as u32, y as u32)) {
            continue;
        }

        let variant = chess_board.clone();
        chess_board.place_queen((x as u32, y as u32));
        {
            chess_boards.lock().unwrap().push_front(chess_board);
        }
        chess_board = variant;
    }
}

pub fn solve_parallel(chess_size: u32, number_of_threads: u32) {
    let chess_boards: Arc<Mutex<VecDeque<ChessBoard>>> = Arc::new(Mutex::new(VecDeque::new()));

    let mut guard = chess_boards.lock().unwrap();
    guard.push_front(ChessBoard::new(chess_size));
    drop(guard);

    let number_of_solutions: Arc<Mutex<u32>> = Arc::new(Mutex::new(0));

    let mut handles = Vec::new();
    for _ in 0..number_of_threads {
        let chess_boards: Arc<Mutex<VecDeque<ChessBoard>>> = Arc::clone(&chess_boards);
        let number_of_solutions: Arc<Mutex<u32>> = Arc::clone(&number_of_solutions);

        let handle = thread::spawn(move || loop {
            let mut guard = chess_boards.lock().unwrap();
            let chess_board = guard.pop_front();
            drop(guard);

            match chess_board {
                None => break,
                Some(chess_board) => {
                    if chess_board.queens.len() == chess_board.size as usize {
                        {
                            *number_of_solutions.lock().unwrap() += 1;
                            if chess_board.size < 7 {
                                println!(
                                    "Solution {}:\n {}",
                                    *number_of_solutions.lock().unwrap(),
                                    chess_board
                                )
                            };
                        }
                    }

                    generate_row_variants(chess_board, &chess_boards);
                }
            }
        });

        handles.push(handle);
    }

    for handle in handles {
        handle.join().unwrap();
    }

    println!(
        "Number of results: {}",
        *number_of_solutions.lock().unwrap()
    );
}
