mult :: ((Int, Int), (Int, Int)) -> ((Int, Int), (Int, Int)) -> ((Int, Int), (Int, Int))
mult ((a1, a2), (a3, a4)) ((b1, b2), (b3, b4)) = (
    (a1 * b1 + a2 * b3, a1 * b2 + a2 * b4),
    (a3 * b1 + a4 * b3, a3 * b2 + a4 * b4)
  )

myMatrixPowHelper :: ((Int, Int), (Int, Int)) -> Int -> ((Int, Int), (Int, Int)) -> ((Int, Int), (Int, Int))
myMatrixPowHelper a 0 p = p
myMatrixPowHelper a n p =
  if odd n
  then myMatrixPowHelper a (n - 1) (p `mult` a)
  else myMatrixPowHelper (a `mult` a) (n `div` 2) p

myMatrixPow :: ((Int, Int), (Int, Int)) -> Int -> ((Int, Int), (Int, Int))
myMatrixPow a b = myMatrixPowHelper a b ((1, 0), (0, 1))

myFibonacci :: Int -> Int
myFibonacci a = snd $ snd $ myMatrixPowHelper ((0, 1), (1, 1)) (a - 1) ((1, 0), (0, 1))