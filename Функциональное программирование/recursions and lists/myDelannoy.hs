delannoyStep :: [Int] -> [Int] -> [Int]
delannoyStep cs ds = 1: (zipWith3 (\x y z -> x + y + z) ds (tail ds) cs) ++ [1]

delannoyHelper :: [Int] -> [Int] -> [[Int]]
delannoyHelper cs ds = cs: (delannoyHelper ds (delannoyStep cs ds))

delannoyLayers :: [[Int]]
delannoyLayers = delannoyHelper [1] [1, 1]

delannoy :: Int -> Int -> Int
delannoy a b = (last $ take (a + b + 1) $ delannoyLayers) !! b