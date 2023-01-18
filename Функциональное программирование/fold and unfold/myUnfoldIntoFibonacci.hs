import Data.List (unfoldr)

myUnfoldIntoFibonacciHelper (a, b) = Just (a, (b, a + b))

myUnfoldIntoFibonacci = unfoldr myUnfoldIntoFibonacciHelper (0, 1)
