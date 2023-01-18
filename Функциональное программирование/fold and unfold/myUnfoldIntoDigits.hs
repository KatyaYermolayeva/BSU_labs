import Data.List (unfoldr)

myUnfoldIntoDigitsHelper 0 = Nothing
myUnfoldIntoDigitsHelper n = Just ((n `mod` 2), (n `div` 2))

myUnfoldIntoDigits n = unfoldr myUnfoldIntoDigitsHelper n