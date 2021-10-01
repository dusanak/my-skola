package main

import (
	"fmt"
	"math"
	"sync/atomic"

	"modernc.org/mathutil"
)

const MAX_POSITIONS = 10
const MAX_GOROUTINES = 1 * 10e5

type Result struct {
	path   IntVector
	length float32
}

func (x Result) Compare(y Result) bool {
	return x.length < y.length
}

type IntVector []int

func (v IntVector) Len() int {
	return len(v)
}
func (v IntVector) Swap(i, j int) {
	v[i], v[j] = v[j], v[i]
}
func (v IntVector) Less(i, j int) bool {
	return v[i] < v[j]
}

type Vertex struct {
	X float32
	Y float32
}

func (v1 Vertex) Distance(v2 Vertex) float32 {
	x_distance := v2.X - v1.X
	y_distance := v2.Y - v1.Y

	total_distance := math.Sqrt(math.Pow(float64(x_distance), 2) + math.Pow(float64(y_distance), 2))

	return float32(total_distance)
}

func shortestPathBruteForce(distance_matrix *[][]float32) ([]int, float32) {
	permutation := make(IntVector, len(*distance_matrix))
	for i := 0; i < len(permutation); i++ {
		permutation[i] = i
	}

	shortest_distance := float32(math.MaxFloat32)
	shortest_path := make(IntVector, len(*distance_matrix))

	for {
		current_distance := float32(0)

		for i := 0; i < len(permutation)-1; i++ {
			current_distance += (*distance_matrix)[permutation[i]][permutation[i+1]]
		}

		current_distance += (*distance_matrix)[permutation[len(permutation)-1]][permutation[0]]

		if current_distance < shortest_distance {
			shortest_distance = current_distance
			copy(shortest_path, permutation)
		}

		if !mathutil.PermutationNext(permutation) {
			break
		}
	}

	return shortest_path, shortest_distance
}

func pathLengthGoroutine(distance_matrix *[][]float32, path IntVector, counter *int32, results chan Result) {
	defer atomic.AddInt32(counter, -1)

	var distance float32 = 0

	for i := 0; i < len(path)-1; i++ {
		distance += (*distance_matrix)[path[i]][path[i+1]]
	}

	distance += (*distance_matrix)[path[len(path)-1]][path[0]]

	results <- Result{path, distance}
}

func shortestPathBruteForceWithGoroutines(distance_matrix *[][]float32) Result {
	permutation := make(IntVector, len(*distance_matrix))
	for i := 0; i < len(permutation); i++ {
		permutation[i] = i
	}

	var counter int32 = 0
	results := make(chan Result, 100)
	best_result := Result{permutation, float32(math.MaxFloat32)}

goroutine_starter:
	for {
		for counter < MAX_GOROUTINES {
			new_path := make(IntVector, len(*distance_matrix))
			copy(new_path, permutation)
			atomic.AddInt32(&counter, 1)
			go pathLengthGoroutine(distance_matrix, new_path, &counter, results)

			if !mathutil.PermutationNext(permutation) {
				break goroutine_starter
			}
		}

		for counter > 0 {
			select {
			case res := <-results:
				if res.length < best_result.length {
					best_result = res
				}
			default:
				break
			}
		}
	}

	fmt.Println("All Goroutines running!")

output_receiver:
	for {
		select {
		case res := <-results:
			if res.length < best_result.length {
				best_result = res
			}
		default:
			if counter == 0 {
				break output_receiver
			}
		}
	}

	return best_result
}

func main() {
	positions := []Vertex{
		{38.24, 20.42},
		{39.57, 26.15},
		{40.56, 25.32},
		{36.26, 23.12},
		{33.48, 10.54},
		{37.56, 12.19},
		{38.42, 13.11},
		{37.52, 20.44},
		{41.23, 9.10},
		{41.17, 13.05},
		{36.08, -5.21},
		{38.47, 15.13},
		{38.15, 15.35},
		{37.51, 15.17},
		{35.49, 14.32},
		{39.36, 19.56},
		{38.09, 24.36},
		{36.09, 23.00},
		{40.44, 13.57},
		{40.33, 14.15},
		{40.37, 14.23},
		{37.57, 22.56}}

	number_of_positions := len(positions)
	if number_of_positions > MAX_POSITIONS {
		number_of_positions = MAX_POSITIONS
	}

	distance_matrix := make([][]float32, number_of_positions)

	for i := 0; i < number_of_positions; i++ {
		for j := 0; j < number_of_positions; j++ {
			distance_matrix[i] = append(distance_matrix[i], positions[i].Distance(positions[j]))
		}
	}

	/*for _, v := range distance_matrix {
		fmt.Println(v)
	}*/

	shortest_path, shortest_distance := shortestPathBruteForce(&distance_matrix)
	fmt.Println("Brute force")
	fmt.Println(shortest_path)
	fmt.Println(shortest_distance)

	/*result := shortestPathBruteForceWithGoroutines(&distance_matrix)
	fmt.Println("Brute force w/ Goroutines")
	fmt.Println(result.path)
	fmt.Println(result.length)*/
}
