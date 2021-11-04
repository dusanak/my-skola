use std::fmt;
use std::{thread, time};

struct GameOfLife {
    size: (u32, u32),
    map: Vec<bool>
}

impl GameOfLife {
    fn new(size: (u32, u32)) -> GameOfLife {
        GameOfLife {
            size: size,
            map: vec![false; (size.0 * size.1) as usize]
        }
    }

    fn get_cell(&self, position: (u32, u32)) -> Option<&bool> {
        self.map.get((position.0 + self.size.0 * position.1) as usize)
    }

    fn set_cell(&mut self, position: (u32, u32), state: bool) {
        *self.map.get_mut((position.0 + self.size.0 * position.1) as usize).unwrap() = state;
    }

    fn set_cells(&mut self, positions: &[(u32, u32)], state: bool) {
        for pos in positions {
            self.set_cell(*pos, state);
        }
    }

    fn number_of_living_neighbours(&self, position: (u32, u32)) -> u32 {
        let mut counter = 0;

        for x in (position.0) as i32 - 1..(position.0) as i32 + 2 {
            for y in (position.1) as i32 - 1..(position.1) as i32 + 2 {
                if (x < 0) || (y < 0) || ((x, y) == (position.0 as i32, position.1 as i32)){
                    continue;
                }
                match self.get_cell((x as u32, y as u32)) {
                    None => (),
                    Some(state) => match *state {
                        false => (),
                        true => counter += 1
                    }
                }
            }
        };

        counter
    }

    fn new_state(&self, position: (u32, u32)) -> bool {
        let current_state = self.get_cell(position).unwrap();

        match self.number_of_living_neighbours(position) {
            0 => false,
            1 => false,
            2 => *current_state,
            3 => true,
            4..=8 => false,
            _ => panic!()
        }
    }

    fn next_generation(&mut self) {
        let mut new_map = vec![false; (self.size.0 * self.size.1) as usize];

        for y in 0..self.size.1 {
            for x in 0..self.size.0 {
                *new_map.get_mut((x + self.size.0 * y) as usize).unwrap() = self.new_state((x, y));
            }
        }

        self.map = new_map
    }
}

impl fmt::Display for GameOfLife {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        for _ in 0..self.size.1 + 2 {
            write!(f, "-").unwrap();
        }
        writeln!(f, "").unwrap();
         
        for y in 0..self.size.1 {
            write!(f, "|").unwrap();
            for x in 0..self.size.0 {
                match self.map.get((x + self.size.0 * y) as usize).unwrap() {
                    true => {
                        write!(f, "*").unwrap();
                    },
                    false => {
                        write!(f, " ").unwrap();
                    }
                }
            }
            writeln!(f, "|").unwrap();
        }

        for _ in 0..self.size.1 + 2 {
            write!(f, "-").unwrap();
        } 

        Ok(())
    }
}

fn main() {
    let mut game = GameOfLife::new((20, 20));

    game.set_cells(&vec![(4, 4), (5, 3), (5, 5), (6, 4)], true);
    game.set_cells(&vec![(8, 8), (8, 9), (8, 10)], true);
    game.set_cells(&vec![(12, 15), (13, 15), (14, 15), (15, 15), (14, 14), (14, 16), (13, 14), (13, 16)], true);

    loop {
        thread::sleep(time::Duration::from_secs(1));
        print!("\x1B[2J\x1B[1;1H");
        println!("{}", game);
        game.next_generation()
    }
}
