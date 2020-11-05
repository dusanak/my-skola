type Result = [String]

pp :: Result -> IO ()
pp x = putStr (concat (map (++"\n") x))

sampleInput = 
   ["....................",
    "....................",
    "....#####...........",
    "...##...##..........",
    "..##.....##.........",
    "..#.......#.........",
    "..#...############..",
    "..#...#...#......#..",
    "..##..#..##......#..",
    "...##.#.##.......#..",
    "....#####........#..",
    "......#..........#..",
    "......############..",
    "....................",
    "...................."]

getChar'::[String] -> (Int, Int) -> Maybe Char
getChar' [] _ = Nothing
getChar' ([]:_) _ = Nothing
getChar' ((char:_):_) (0, 0) = Just char
getChar' ((_:row):rows) (0, y) = getChar' (row:rows) (0, y - 1)
getChar' (_:rows) (x, y) = getChar' rows (x - 1, y)

changeCharInRow::String -> Int -> Char -> String
changeCharInRow (_:row) 0 new_char = new_char : row 
changeCharInRow (char:row) pos new_char = char : (changeCharInRow row (pos - 1) new_char) 

changeChar::[String] -> (Int,Int) -> Char -> Result
changeChar (row:rows) (0, y) player = (changeCharInRow row y player) : rows
changeChar (row:rows) (x, y) player = row : (changeChar rows (x - 1, y) player)

fill :: Result -> (Int,Int) -> Result
fill picture (x, y) = case char of
                    Nothing -> picture
                    Just value 
                        | (value == '*') || (value == '#') -> picture
                        | otherwise -> fill (fill (fill (fill (changeChar picture (x, y) '*') (x - 1, y)) (x + 1, y)) (x, y - 1)) (x, y + 1)
                    where char = getChar' picture (x, y)
