import Data.List (unfoldr)

isNotMultipleOf x y = y `mod` x /= 0

primesHelper (x : xs) = Just (x, filter (isNotMultipleOf x) xs)

primes = unfoldr primesHelper (2 : [3, 5 ..])