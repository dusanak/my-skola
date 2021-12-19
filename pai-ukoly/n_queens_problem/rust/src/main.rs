use std::fmt;

struct ChessBoard {
    size: u32,
    queens: Vec<(u32, u32)>
}

impl ChessBoard {
    fn new(size: u32) -> ChessBoard {
        ChessBoard {
            size: size,
            queens: Vec::new()
        }
    }

    fn place_queen(&mut self, position: (u32, u32)) -> bool {
        if !self.can_place_queen(position) {
            return false
        };

        self.queens.push(position);
        true
    }

    fn can_place_queen(&mut self, position: (u32, u32)) -> bool {
        self.is_horizontal_free(position) && 
        self.is_vertical_free(position) &&
        self.is_diagonal_free(position)
    }

    fn is_horizontal_free(&mut self, position: (u32, u32)) -> bool {
        for queen in &self.queens {
            if queen.1 == position.1 {
                return false
            }
        }
        true
    }

    fn is_vertical_free(&mut self, position: (u32, u32)) -> bool {
        for queen in &self.queens {
            if queen.0 == position.0 {
                return false
            }
        }
        true
    }

    fn is_diagonal_free(&mut self, position: (u32, u32)) -> bool {
        for queen in &self.queens {
            if (queen.0 as i32 - queen.1 as i32) == (position.0 as i32 - position.1 as i32) {
                return false
            }
        }
        true
    }
}

impl fmt::Display for ChessBoard {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "  ").unwrap();
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

fn main() {
    let mut chess_board = ChessBoard::new(4);
    println!("{}", chess_board);
    chess_board.place_queen((0, 0));
    println!("{}", chess_board);
    chess_board.place_queen((0, 1));
    chess_board.place_queen((1, 0));
    chess_board.place_queen((1, 1));
    chess_board.place_queen((1, 2));
    println!("{}", chess_board);
}
