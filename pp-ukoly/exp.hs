--domaci ukol vypracovat funkce co jsou na strankach cviceni

sumCustom :: Int -> Int -> Int
sumCustom x y = x + y

fact :: Int -> Int
fact 0 = 1
fact x = x * fact (x - 1)

fib :: Int -> Int
fib 0 = 0
fib 1 = 1
fib x = fib (x - 1) + fib (x - 2)

fibIter :: Int -> Int
fibIter n = fibInner n 0 1

fibInner :: Int -> Int -> Int -> Int
fibInner 0 a b = a
fibInner n a b = fibInner (n - 1) b (a + b)

equal :: Int -> Int -> Bool
equal a b | a == b = True
          | otherwise = False

sumit :: [Int] -> Int
sumit [] = 0
sumit (x:xs) = x + sumit xs

leapYear :: Int -> Bool
leapYear x | mod x 400 == 0 = True
           | mod x 100 == 0 = False
           | mod x 4 == 0 = True
           | otherwise = False

gcd' :: Int -> Int -> Int
gcd' x y | x > y = gcd' (x - y) y
         | x < y = gcd' x (y - x)
         | otherwise = x

getHead :: [a] -> a
getHead (x:xs) = x

getLast :: [a] -> a
getLast [x] = x
getLast (_:xs) = getLast xs

isElement :: Eq a => a -> [a] -> Bool
isElement x (y:ys) | x == y = True
                     | otherwise = isElement x ys
isElement x [] = False

combine :: [a] -> [a] -> [a]
combine [] y = y
combine (x:xs) y = (x:combine xs y)

bubble :: [Int] -> [Int]
bubble [] = []
bubble [x] = [x]
bubble (x1:x2:xs) | x1 > x2 = x2:(bubble (x1:xs))
                  | otherwise = x1:(bubble (x2:xs))

isSorted :: [Int] -> Bool
isSorted [] = True
isSorted [x] = True
isSorted (x1:x2:xs) | x1 <= x2 = isSorted (x2:xs)
                    | x2 < x1 = False

bubbleSort :: [Int] -> [Int]
bubbleSort x | isSorted x == True = x
             | otherwise = bubbleSort (bubble x)