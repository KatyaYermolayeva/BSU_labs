myGcd :: Int -> Int -> Int
myGcd a b = 
  if a > b 
  then myGcd (a-b) b
  else if b > a
  then myGcd a (b-a)
  else a