toDigitsR :: Int -> Int -> [Int]
toDigitsR 0 n = []
toDigitsR x n =
  let (d, m) = divMod x n
   in m : toDigitsR d n

toDigits :: Int -> Int -> [Int]
toDigits x n = reverse $ toDigitsR x n

fromDigitsHelper :: [Int] -> Int -> Int -> Int -> Int
fromDigitsHelper [] b c d = d
fromDigitsHelper (a:as) b c d = fromDigitsHelper as b (c * b) (d + a * c) 

fromDigits :: [Int] -> Int -> Int
fromDigits [] a = 0
fromDigits a b = fromDigitsHelper (reverse a) b 1 0

addDigitsHelper :: [Int] -> [Int] -> Int -> Int -> [Int]
addDigitsHelper [] [] n 0 = []
addDigitsHelper [] [] n t = [t]
addDigitsHelper a [] n t = addDigitsHelper a [0] n t
addDigitsHelper [] b n t = addDigitsHelper [0] b n t
addDigitsHelper (a : as) (b : bs) n t =
  let (d, m) = divMod (a + b + t) n
   in m : addDigitsHelper as bs n d

addDigits :: [Int] -> [Int] -> Int -> [Int]
addDigits a b n = reverse $ addDigitsHelper (reverse a) (reverse b) n 0