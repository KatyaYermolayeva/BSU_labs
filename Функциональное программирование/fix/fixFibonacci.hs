import Data.Function

fibonacci :: Num a => [a]
fibonacciHelper :: Num a => [a] -> [a]
fibonacciHelper as = 1 : (1 : zipWith (+) as (tail as))
fibonacci = fix fibonacciHelper

fibonacciN :: Num a => Int -> a
fibonacciNHelper :: Num a => (Int -> a -> a -> a) -> Int -> a -> a -> a
fibonacciNHelper f k p q = if k < 2 then q else f (k - 1) q (p + q) 
fibonacciN n = (fix fibonacciNHelper) n 1 1