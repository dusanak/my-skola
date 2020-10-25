type Result = [String]

pp :: Result -> IO ()
pp x = putStr (concat (map (++"\n") x))

flipV :: Result -> Result
flipV [row] = [row]
flipV (row:rows) = (flipV rows) ++ [row] 

generateRow::Int -> Char -> String
generateRow 0 _ = "" 
generateRow 1 ' ' = "|"
generateRow x ' ' = ' ' : (generateRow (x - 1) ' ')
generateRow x '|' = '|' : (generateRow (x - 1) ' ')
generateRow x '-' = '-' : (generateRow (x - 1) '-')

generateMap::(Int,Int) -> Bool -> Result
generateMap (0, y) False = [(generateRow (y + 2) '-')]
generateMap (x, y) False = (generateRow (y + 2) '|') : (generateMap (x - 1, y) False)
generateMap (x, y) True = (generateRow (y + 2) '-') : (generateMap (x, y) False)

changeColumn::String -> Int -> Char -> String
changeColumn (_:row) 0 player = player : row 
changeColumn (letter:row) pos player = letter : (changeColumn row (pos - 1) player) 

changeRow::[String] -> (Int,Int) -> Char -> Result
changeRow (row:rows) (0, y) player = (changeColumn row y player) : rows
changeRow (row:rows) (x, y) player = row : (changeRow rows (x - 1, y) player)

tickTackGame::[String] -> [(Int,Int)] -> Char -> Result
tickTackGame gamemap [] _ = gamemap
tickTackGame gamemap (step:steps) 'x' = tickTackGame (changeRow gamemap step 'x') steps 'o'
tickTackGame gamemap (step:steps) 'o' = tickTackGame (changeRow gamemap step 'o') steps 'x'

ticktack::(Int,Int) -> [(Int,Int)] -> Result
ticktack size steps = flipV (tickTackGame (generateMap size True) steps 'x')

getLetter::[String] -> (Int, Int) -> Char
getLetter ((letter:_):_) (0, 0) = letter
getLetter ((_:row):rows) (0, y) = getLetter (row:rows) (0, y - 1)
getLetter (_:rows) (x, y) = getLetter rows (x - 1, y)

checkMap::[String] -> (Int,Int) -> Bool
checkMap _ (0, _) = False
checkMap gamemap (x, y) | checkRow gamemap (x, y) == True = True
                        | otherwise = checkMap gamemap (x - 1, y)

checkRow::[String] -> (Int, Int) -> Bool
checkRow _ (_, 0) = False
checkRow gamemap (x, y) | (letter /= 'o') && (letter /= 'x') = checkRow gamemap (x, y - 1)
                        | checkHorizontal gamemap letter (x, y) 4 == True = True
                        | checkVertical gamemap letter (x, y) 4 == True = True
                        | checkMainDiagonal gamemap letter (x, y) 4 == True = True
                        | checkMinorDiagonal gamemap letter (x, y) 4 == True = True
                        | otherwise = checkRow gamemap (x, y - 1)
                        where letter = getLetter gamemap (x, y)

checkHorizontal::[String] -> Char -> (Int, Int) -> Int -> Bool
checkHorizontal _ _ _ 0 = True
checkHorizontal gamemap player (x, y) count | letter == player = checkHorizontal gamemap player (x, y - 1) (count - 1)
                                            | otherwise = False
                                            where letter = getLetter gamemap (x, y)

checkVertical::[String] -> Char -> (Int, Int) -> Int -> Bool
checkVertical _ _ _ 0 = True
checkVertical gamemap player (x, y) count | letter == player = checkVertical gamemap player (x - 1, y) (count - 1)
                                            | otherwise = False
                                            where letter = getLetter gamemap (x, y)

checkMainDiagonal::[String] -> Char -> (Int, Int) -> Int -> Bool
checkMainDiagonal _ _ _ 0 = True
checkMainDiagonal gamemap player (x, y) count | letter == player = checkMainDiagonal gamemap player (x - 1, y - 1) (count - 1)
                                              | otherwise = False
                                              where letter = getLetter gamemap (x, y)

checkMinorDiagonal::[String] -> Char -> (Int, Int) -> Int -> Bool
checkMinorDiagonal _ _ _ 0 = True
checkMinorDiagonal gamemap player (x, y) count | letter == player = checkMinorDiagonal gamemap player (x - 1, y + 1) (count - 1)
                                               | otherwise = False
                                               where letter = getLetter gamemap (x, y)

winner::(Int,Int) -> [(Int,Int)] -> Bool
winner (x, y) steps = checkMap (ticktack (x, y) steps) (x + 1, y + 1)

-- Test cases
-- (8, 8) [(1, 1), (2, 1), (1, 2), (2, 2), (1, 3), (2, 3), (1, 4)]
-- (8, 8) [(1, 1), (2, 1), (1, 2), (2, 2), (1, 3), (2, 3), (1, 5)]
-- (8, 8) [(1, 1), (2, 1), (2, 2), (1, 2), (3, 3), (2, 3), (4, 4)]
-- (8, 8) [(1, 1), (2, 2), (2, 1), (1, 2), (3, 1), (2, 3), (4, 1)]
-- (8, 8) [(1, 1), (2, 2), (2, 1), (1, 2), (3, 1), (2, 3), (5, 1)]