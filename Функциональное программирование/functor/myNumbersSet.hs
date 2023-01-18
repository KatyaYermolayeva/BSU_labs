import Data.List hiding (union)


class NumSet s where
  contains :: s -> Int -> Bool
  toList :: s -> [Int]
  fromList :: [Int] -> s
  intersection :: s -> s -> s
  union :: s -> s -> s
  difference :: s -> s -> s

instance NumSet [Int] where
  contains [] _ = False
  contains (b: bs) x = (b == x) || contains bs x
  toList x = x
  fromList x = x
  intersection a b = difference a (difference a b)
  union a b = a ++ difference b a
  difference (a : as) b
    | contains b a = difference as b
    | otherwise = a : difference as b

instance NumSet [Bool] where
  contains [] _ = False
  contains b x = b !! x
  toList [] = []
  toList b = toListHelper b 1
  fromList [] = []
  fromList x = fromListHelper (sort x)
  intersection a b = difference a (difference a b)
  union [] b = b
  union a [] = a
  union (a : as) (b : bs) = (a || b) : (union as bs)
  difference [] b = []
  difference a [] = a
  difference (a:as) (b : bs) = (a && not b) : (difference as bs)

toListHelper [] c = []
toListHelper (b:bs) c 
   | b = c : toListHelper bs (c + 1)
   | otherwise = toListHelper bs (c + 1)

fromListHelper [] = []
fromListHelper (b : bs) = (replicate (b - 1) False) ++ [True] ++ fromListHelper (map (subtract b) bs)
