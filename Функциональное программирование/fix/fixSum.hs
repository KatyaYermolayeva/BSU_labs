import Data.Function

lsum :: Num a => [a] -> a
lsumHelper :: Num a => ([a] -> a -> a) -> [a] -> a -> a
lsumHelper f [] s = s
lsumHelper f (x : xs) s = f xs (s + x)
lsum x = (fix lsumHelper) x 0
