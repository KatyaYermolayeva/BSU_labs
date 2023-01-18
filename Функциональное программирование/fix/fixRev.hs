import Data.Function

lrev :: [a] -> [a]
lrevHelper :: ([a] -> [a] -> [a]) -> [a] -> [a] -> [a]
lrevHelper f [] ns = ns
lrevHelper f (x : xs) ns = f xs (x : ns)
lrev x = (fix lrevHelper) x []