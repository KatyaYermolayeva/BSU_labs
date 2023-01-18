import Data.List (unfoldr)

myUnfoldIntoSyracuseHelper 0 = Nothing
myUnfoldIntoSyracuseHelper 1 = Just (1, 0)
myUnfoldIntoSyracuseHelper n
  | odd n = Just (n, 3 * n + 1)
  | otherwise = Just (n, n `div` 2)

myUnfoldIntoSyracuse n = unfoldr myUnfoldIntoSyracuseHelper n