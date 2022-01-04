package main

import (
	"bytes"
	"errors"
	"fmt"
	"os"
	"strconv"
)

type ChessboardStack struct {
	stack []ChessBoard
	size  int
}

func (cs *ChessboardStack) Push(chessboard *ChessBoard) {
	if cs.size == cap(cs.stack) {
		cs.stack = append(cs.stack, *chessboard)
	} else {
		cs.stack[cs.size] = *chessboard
	}
	cs.size += 1
}

func (cs *ChessboardStack) Pop() (ChessBoard, error) {
	if cs.size == 0 {
		return ChessBoard{}, errors.New("stack is empty")
	} else {
		result := cs.stack[cs.size]
		cs.size -= 1
		return result, nil
	}
}

func (cs *ChessboardStack) IsEmpty() bool {
	return cs.size == 0
}

type Queen struct {
	x int
	y int
}

type ChessBoard struct {
	size   int
	queens []Queen
}

func (cb *ChessBoard) DeepCopy() ChessBoard {
	result := ChessBoard{cb.size, make([]Queen, len(cb.queens), cap(cb.queens))}
	copy(result.queens, cb.queens)
	return result
}

func (chessboard ChessBoard) String() string {
	var b bytes.Buffer

	b.WriteByte(' ')
	b.WriteByte(' ')
	for x := 0; x < chessboard.size; x++ {
		b.WriteByte(byte(x + 48))
	}
	b.WriteByte('\n')

	chessboard_representation := make([]byte, chessboard.size*chessboard.size)
	for i := range chessboard_representation {
		chessboard_representation[i] = '.'
	}

	for _, queen := range chessboard.queens {
		chessboard_representation[queen.x+queen.y*chessboard.size] = 'Q'
	}

	for y := 0; y < chessboard.size; y++ {
		b.WriteByte(byte(y + 48))
		b.WriteByte(' ')
		for x := 0; x < chessboard.size; x++ {
			b.WriteByte(chessboard_representation[x+y*chessboard.size])
		}
		b.WriteByte('\n')
	}

	return b.String()
}

func (chessboard *ChessBoard) place_queen(new_queen *Queen) bool {
	if !chessboard.can_place_queen(new_queen) {
		return false
	}

	chessboard.queens = append(chessboard.queens, *new_queen)
	return true
}

func (chessboard *ChessBoard) can_place_queen(new_queen *Queen) bool {
	return chessboard.is_horizontal_free(new_queen) &&
		chessboard.is_vertical_free(new_queen) &&
		chessboard.is_diagonal_free(new_queen)
}

func (chessboard *ChessBoard) is_horizontal_free(new_queen *Queen) bool {
	for _, queen := range chessboard.queens {
		if new_queen.y == queen.y {
			return false
		}
	}
	return true
}

func (chessboard *ChessBoard) is_vertical_free(new_queen *Queen) bool {
	for _, queen := range chessboard.queens {
		if new_queen.x == queen.x {
			return false
		}
	}
	return true
}

func (chessboard *ChessBoard) is_diagonal_free(new_queen *Queen) bool {
	for _, queen := range chessboard.queens {
		if (queen.x - queen.y) == (new_queen.x - new_queen.y) {
			return false
		}

		if (queen.x + queen.y) == (new_queen.x + new_queen.y) {
			return false
		}
	}
	return true
}

func solve_serial(chess_board *ChessBoard) int {
	y := len(chess_board.queens)

	if y == chess_board.size {
		return 1
	}

	result := 0

	for x := 0; x < chess_board.size; x++ {
		if !chess_board.can_place_queen(&Queen{x, y}) {
			continue
		}

		new_chess_board := chess_board.DeepCopy()
		new_chess_board.place_queen(&Queen{x, y})
		result += solve_serial(&new_chess_board)
	}

	return result
}

func solve_parallel(chess_board *ChessBoard, result_c chan int) {
	y := len(chess_board.queens)

	if y == chess_board.size {
		result_c <- 1
	}

	running_goroutines := 0
	new_c := make(chan int)

	for x := 0; x < chess_board.size; x++ {
		if !chess_board.can_place_queen(&Queen{x, y}) {
			continue
		}

		new_chess_board := chess_board.DeepCopy()
		new_chess_board.place_queen(&Queen{x, y})

		go solve_parallel(&new_chess_board, new_c)
		running_goroutines += 1
	}

	result := 0

	for i := 0; i < running_goroutines; i++ {
		result += <-new_c
	}

	result_c <- result
}

func main() {
	chess_board_size := 4
	is_parallel := true

	if len(os.Args) > 1 {
		chess_board_size, _ = strconv.Atoi(os.Args[1])
	}

	if len(os.Args) > 2 {
		is_parallel, _ = strconv.ParseBool(os.Args[2])
	}

	result := 0
	if !is_parallel {
		fmt.Println("Serial solution for", chess_board_size, "queens")
		result = solve_serial(&ChessBoard{chess_board_size, make([]Queen, 0, chess_board_size)})
	} else {
		fmt.Println("Parallel solution for", chess_board_size, "queens")

		result_c := make(chan int)
		go solve_parallel(&ChessBoard{chess_board_size, make([]Queen, 0, chess_board_size)}, result_c)
		result = <-result_c
	}

	fmt.Println("Number of results:", result)
}
