use std::fmt;
use std::thread;
use std::time;
use std::sync::Arc;
use std::sync::Mutex;
use std::sync::atomic::{AtomicBool, AtomicU32, AtomicUsize, Ordering};

const LOCAL_STACK_SIZE: usize = 8;

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
    chess_boards: &mut Vec<ChessBoard>,
) {
    let y = chess_board.queens.len();

    for x in 0..chess_board.size as usize {
        if !chess_board.can_place_queen((x as u32, y as u32)) {
            continue;
        }

        let variant = chess_board.clone();
        chess_board.place_queen((x as u32, y as u32));
        {
            chess_boards.push(chess_board);
        }
        chess_board = variant;
    }
}

pub fn solve_parallel(chess_size: u32, number_of_threads: u32) {
    let shared_chess_boards: Arc<Mutex<Vec<ChessBoard>>> = Arc::new(Mutex::new(Vec::new()));

    let mut guard = shared_chess_boards.lock().unwrap();
    guard.push(ChessBoard::new(chess_size));
    drop(guard);

    let sleeping_threads = Arc::new(AtomicU32::new(0));
    let is_work_finished = Arc::new(AtomicBool::new(false));
    
    let number_of_solutions = Arc::new(AtomicU32::new(0));
    let max_shared = Arc::new(AtomicUsize::new(0));

    let mut handles = Vec::new();
    for _ in 0..number_of_threads {
        let shared_chess_boards: Arc<Mutex<Vec<ChessBoard>>> = Arc::clone(&shared_chess_boards);
        let sleeping_threads = Arc::clone(&sleeping_threads);
        let is_work_finished = Arc::clone(&is_work_finished);
        
        let number_of_solutions = Arc::clone(&number_of_solutions);
        let max_shared = Arc::clone(&max_shared);

        let handle = thread::spawn(move || {
            let mut local_chess_boards: Vec<ChessBoard> = Vec::new();
            let mut _counter = 0;

            loop {
                let chess_board = local_chess_boards.pop();

                match chess_board {
                    None => {
                        // println!("I am empty! {:?}", thread::current().id());
                        let mut guard = shared_chess_boards.lock().unwrap();
                        match guard.pop() {
                            None => {
                                // println!("Sleeping {:?}", thread::current().id());
                                sleeping_threads.fetch_add(1, Ordering::Relaxed);
                                // println!("Sleeping threads: {}", sleeping_threads.load(Ordering::Relaxed));
                                drop(guard);
                                if sleeping_threads.load(Ordering::Relaxed) == number_of_threads || is_work_finished.load(Ordering::Relaxed) {
                                    // println!("Work finished!! {:?}", thread::current().id());
                                    is_work_finished.store(true, Ordering::Relaxed);
                                    break;
                                }
                                thread::sleep(time::Duration::from_millis(5));
                                sleeping_threads.fetch_sub(1, Ordering::Relaxed);
                            },
                            Some(chess_board) => {
                                if guard.len() > max_shared.load(Ordering::Relaxed) {
                                    // println!("Current max: {}, new max: {}", max_shared.load(Ordering::Relaxed), guard.len());                                               
                                    max_shared.store(guard.len(), Ordering::Relaxed);
                                }

                                // println!("Got shared chess board! {:?} Remaining: {}.", thread::current().id(), guard.len());
                                local_chess_boards.push(chess_board);
                            }
                        }
                    }
                    Some(chess_board) => {
                        _counter += 1;
                        if chess_board.queens.len() == chess_board.size as usize {
                            {
                                number_of_solutions.fetch_add(1, Ordering::Relaxed);
                                if chess_board.size < 7 {
                                    println!(
                                        "Solution {}:\n {}",
                                        number_of_solutions.load(Ordering::Relaxed),
                                        chess_board
                                    )
                                };
                            }
                        }

                        generate_row_variants(chess_board, &mut local_chess_boards);
                        // println!("Number of local chess boardS {}.", local_chess_boards.len());
                    }
                }

                if local_chess_boards.len() > LOCAL_STACK_SIZE {
                    let mut guard = shared_chess_boards.lock().unwrap();
                    guard.extend(local_chess_boards.drain(LOCAL_STACK_SIZE..));
                    // println!("Local stack is full: {}.", local_chess_boards.len())                    
                }
            }
            // println!("Thread {:?} is finished! Loop count {}.", thread::current().id(), _counter);
        });

        handles.push(handle);
    }

    for handle in handles {
        handle.join().unwrap();
    }

    // println!("Max shared items: {}", max_shared.load(Ordering::Relaxed));
    
    println!("Number of results: {}", number_of_solutions.load(Ordering::Relaxed));
}
