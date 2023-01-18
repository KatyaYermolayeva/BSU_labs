clone :: Int -> [a] -> [a]
clone 0 a = []
clone n [] = []
clone n (a:as) = replicate n a ++ clone n as