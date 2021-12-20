use std::collections::VecDeque;
use std::fmt;

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

fn generate_row_variants(mut chess_board: ChessBoard, chess_boards: &mut VecDeque<ChessBoard>) {
    let y = chess_board.queens.len();

    for x in 0..chess_board.size as usize {
        if !chess_board.can_place_queen((x as u32, y as u32)) {
            continue;
        }

        let variant = chess_board.clone();
        chess_board.place_queen((x as u32, y as u32));
        chess_boards.push_front(chess_board);
        chess_board = variant;
    }
}

pub fn solve_serial(chess_size: u32) {
    let mut chess_boards = VecDeque::new();
    chess_boards.push_front(ChessBoard::new(chess_size));

    let mut results = 0;

    loop {
        match chess_boards.pop_front() {
            None => break,
            Some(chess_board) => {
                if chess_board.queens.len() == chess_board.size as usize {
                    results += 1;
                    if chess_board.size < 7 {
                        println!("Solution {}:\n {}", results, chess_board)
                    };
                }

                generate_row_variants(chess_board, &mut chess_boards);
            }
        }
    }

    println!("Number of results: {}", results);
}
