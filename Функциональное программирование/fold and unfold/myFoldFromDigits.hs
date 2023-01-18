import Data.List (unfoldr)

myFoldFromDigitsHelper (b, p) a
  | a == 1 = (b + p, p * 2)
  | otherwise = (b, p * 2)

myFoldFromDigits d = fst (foldl myFoldFromDigitsHelper (0, 1) d)