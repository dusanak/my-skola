type Result = [String]

pp :: Result -> IO ()
pp x = putStr (concat (map (++"\n") x))

flipH :: Result -> Result
flipH [row] = [row]
flipH (row:rows) = (flipH rows) ++ [row] 

generateRow::Int -> Char -> String
generateRow 0 _ = "" 
generateRow 1 '.' = "|"
generateRow x '.' = '.' : (generateRow (x - 1) '.')
generateRow x '|' = '|' : (generateRow (x - 1) '.')
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
ticktack size steps = flipH (tickTackGame (generateMap size True) steps 'x')