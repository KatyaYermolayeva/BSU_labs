collatzHelper :: Int -> Int -> Int
collatzHelper 1 n = n
collatzHelper a n =
  if odd a
    then collatzHelper (3 * a + 1) (n + 1)
    else collatzHelper (div a 2) (n + 1)

collatz :: Int -> Int
collatz a = collatzHelper a 1
