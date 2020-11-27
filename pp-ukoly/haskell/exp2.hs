take' :: Int -> [a] -> [a]
take' 0 _ = []
take' _ [] = []
take' n (x:xs) = (x: take (n - 1) xs)

quicksort :: (Ord a) => [a] -> [a]
quicksort [] = []
quicksort [x] = [x]
quicksort (x:xs) = quicksort (filter (<x) xs) ++ x : quicksort (filter (>x) xs)

dotProduct :: [a] -> [b] -> [(a,b)]
dotProduct [] _ = []
dotProduct _ [] = []
dotProduct [x] [y] = [(x,y)]
dotProduct [x] (y:ys) = dotProduct [x] [y] ++ dotProduct [x] ys
dotProduct (x:xs) y = dotProduct [x] y ++ dotProduct xs y 