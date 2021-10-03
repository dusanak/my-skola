use std::time::Instant;
use std::time::Duration;
use std::collections::VecDeque;
use std::sync::Arc;
use std::sync::Mutex;
use std::sync::mpsc;
use std::thread;
use itertools::Itertools;

const MAX_POSITIONS: usize = 12;
const MAX_THREADS: usize = 1;

struct Point(f32, f32);

impl Point {
    fn distance(&self, another_point: &Point) -> f32 {
        let x2 = (another_point.0 - self.0).powi(2);
        let y2 = (another_point.1 - self.1).powi(2);

        (x2 + y2).sqrt()
    }
}

fn brute_force_tsp(distance_matrix: &Vec<Vec<f32>>) -> (Vec<usize>, f32) {
    let mut shortest_path: Vec<usize> = vec![];
    let mut shortest_distance = f32::MAX;
    
    for permutation in (0..distance_matrix.len()).permutations(distance_matrix.len()) {
        let mut current_shortest_distance = 0.0;
        for position in 0..permutation.len() - 1 {
            current_shortest_distance += distance_matrix[permutation[position]][permutation[position + 1]];
        }
        current_shortest_distance += distance_matrix[permutation[permutation.len() - 1]][permutation[0]];
        
        if current_shortest_distance < shortest_distance {
            shortest_path = permutation;
            shortest_distance = current_shortest_distance;
        }
    }

    (shortest_path, shortest_distance)
}

fn brute_force_tsp_w_threads(distance_matrix: Arc<Vec<Vec<f32>>>) -> (Vec<usize>, f32) {   
    let shared_queue: Arc<Mutex<VecDeque<Option<Vec<usize>>>>> = Arc::new(Mutex::new(VecDeque::new()));
    let (tx, rx) = mpsc::channel();
    let mut handles = vec![];

    for _ in 0..MAX_THREADS {
        let shared_queue = Arc::clone(&shared_queue);
        let tx = tx.clone();
        let distance_matrix = Arc::clone(&distance_matrix);
        let handle = thread::spawn(move || {
            loop {
                let permutation;
                {
                    let mut queue = shared_queue.lock().unwrap();
                    permutation = (*queue).pop_front();
                }

                let permutation = match permutation {
                    Some(x) => x,
                    None => {
                        println!("Zzzzzz... {:?}", thread::current().id());
                        thread::sleep(Duration::from_millis(500));
                        continue
                    }
                };
                
                match permutation {
                    None => return,
                    Some(permutation) => {
                        let mut current_shortest_distance = 0.0;
                        for position in 0..permutation.len() - 1 {
                            current_shortest_distance += distance_matrix[permutation[position]][permutation[position + 1]];
                        }
                        current_shortest_distance += distance_matrix[permutation[permutation.len() - 1]][permutation[0]];

                        tx.send((permutation, current_shortest_distance)).unwrap();
                    }
                }
            } 
        });
        handles.push(handle);
    }
    drop(tx);
    
    let start = Instant::now();
    for permutation in (0..distance_matrix.len()).permutations(distance_matrix.len()) {
        let mut queue = shared_queue.lock().unwrap();
        (*queue).push_back(Some(permutation));        
    }
    let duration = start.elapsed();
    println!("Execution time new permutations: {:?}", duration);

    for _ in 0..MAX_THREADS {
        let mut queue = shared_queue.lock().unwrap();
        (*queue).push_back(None);       
    }

    let mut shortest_path: Vec<usize> = vec![];
    let mut shortest_distance = f32::MAX;

    for msg in rx {
        if msg.1 < shortest_distance {
            shortest_path = msg.0;
            shortest_distance = msg.1;
        }
    }

    for handle in handles {
        handle.join().unwrap();
    }

    (shortest_path, shortest_distance)
}

fn main() {
    let positions = vec![
		Point(38.24, 20.42),
		Point(39.57, 26.15),
		Point(40.56, 25.32),
		Point(36.26, 23.12),
		Point(33.48, 10.54),
		Point(37.56, 12.19),
		Point(38.42, 13.11),
		Point(37.52, 20.44),
		Point(41.23, 9.10),
		Point(41.17, 13.05),
		Point(36.08, -5.21),
		Point(38.47, 15.13),
		Point(38.15, 15.35),
		Point(37.51, 15.17),
		Point(35.49, 14.32),
		Point(39.36, 19.56),
		Point(38.09, 24.36),
		Point(36.09, 23.00),
		Point(40.44, 13.57),
		Point(40.33, 14.15),
		Point(40.37, 14.23),
		Point(37.57, 22.56)];

    let mut distance_matrix = vec![vec![0.0; MAX_POSITIONS]; MAX_POSITIONS];

    let number_of_positions = if positions.len() < MAX_POSITIONS {positions.len()} else {MAX_POSITIONS};

    for i in 0..number_of_positions {
        for j in 0..number_of_positions {
            distance_matrix[i][j] = positions[i].distance(&positions[j]);
        }
    }

    // println!("{:?}", distance_matrix);

    let start = Instant::now();
    let (shortest_path, shortest_distance) = brute_force_tsp(&distance_matrix);
    let duration = start.elapsed();
    println!("Brute force");
    println!("{:?}: {}", shortest_path, shortest_distance);
    println!("Execution time: {:?}", duration);

    let start = Instant::now();
    let (shortest_path, shortest_distance) = brute_force_tsp_w_threads(Arc::new(distance_matrix));
    let duration = start.elapsed();
    println!("Brute force with threads");
    println!("{:?}: {}", shortest_path, shortest_distance);
    println!("Execution time: {:?}", duration);
}
