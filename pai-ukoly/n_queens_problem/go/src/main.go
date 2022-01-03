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
		return ChessBoard{}, errors.New("Stack is empty")
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

func generate_row_variants(chess_board ChessBoard, stack *ChessboardStack) {
	y := len(chess_board.queens)

	for x := 0; x < chess_board.size; x++ {
		if !chess_board.can_place_queen(&Queen{x, y}) {
			continue
		}

	}
}

func main() {
	chess_board_size := 4
	number_of_threads := 1

	if len(os.Args) > 1 {
		chess_board_size, _ = strconv.Atoi(os.Args[1])
		number_of_threads, _ = strconv.Atoi(os.Args[2])
	}

	fmt.Println("N Queens problem solver launched for", chess_board_size, "queens and using", number_of_threads, "threads.")

	chessboard := ChessBoard{4, make([]Queen, 0, 4)}
	chessboard.place_queen(&Queen{1, 1})
	chessboard.place_queen(&Queen{1, 2})
	chessboard.place_queen(&Queen{2, 2})
	chessboard.place_queen(&Queen{2, 3})

	fmt.Println(chessboard)

	chess_boards := []ChessBoard{ChessBoard{chess_board_size, make([]Queen, 0, chess_board_size)}}
	results := 0

	// TODO Probably replace generate_row_variants
	// which implies iterative implementation as in Rust
	// with a recursive divide & conquer approach using goroutines
	// in a function which generates variants row by row and
	// calls itself to solve the new variants.
	// when it gets to number of queens == N
	// return a number which get summed up in the above function.

	for {

	}
}
