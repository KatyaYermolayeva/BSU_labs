dividers :: Int -> [Int]
dividers n = dividersHelper n (n - 1)

dividersHelper :: Int -> Int -> [Int]
dividersHelper _ 0 = []
dividersHelper n d =
  if n `mod` d == 0
    then d : dividersHelper n (d - 1)
    else dividersHelper n (d - 1)

isPerfect :: Int -> Bool
isPerfect n = n == sum (dividers n)