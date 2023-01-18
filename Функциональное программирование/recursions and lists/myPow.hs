myPowHelper :: Int -> Int -> Int -> Int
myPowHelper a 0 p = p
myPowHelper a n p =
  if odd n
  then myPowHelper a (n - 1) (p * a)
  else myPowHelper (a*a) (n `div` 2) p

myPow :: Int -> Int -> Int
myPow a b = myPowHelper a b 1