import Data.List (unfoldr)

myUnfoldIntoDenominatorsHelper (1, _) = Nothing
myUnfoldIntoDenominatorsHelper (n, d)
  | n `mod` d == 0 = Just (d, (n `div` d, d))
  | otherwise = myUnfoldIntoDenominatorsHelper (n, d + 1)

myUnfoldIntoDenominators n = unfoldr myUnfoldIntoDenominatorsHelper (n, 2)