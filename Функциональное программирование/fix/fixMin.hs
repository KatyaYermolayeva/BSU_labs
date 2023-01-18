import Data.Function

lmin :: Ord a => [a] -> a
lminHelper :: Ord a => ([a] -> a -> a) -> [a] -> a -> a
lminHelper f [] m = m
lminHelper f (x : xs) m = if x < m then f xs x else f xs m
lmin (x: xs) = (fix lminHelper) xs x
