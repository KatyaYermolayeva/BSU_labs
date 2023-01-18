addToEach x l = map (flip (++) [x]) l

delannoyPathsStep :: [[[Int]]] -> [[[Int]]] -> [[[Int]]]
delannoyPathsStep cs ds = (addToEach 2 (head ds)) : (zipWith3 (\x y z -> (addToEach 0 x) ++ (addToEach 2 y) ++ (addToEach 1 z)) ds (tail ds) cs) ++ [addToEach 0 (last ds)]

delannoyPathsHelper :: [[[Int]]] -> [[[Int]]] -> [[[[Int]]]]
delannoyPathsHelper cs ds = cs : (delannoyPathsHelper ds (delannoyPathsStep cs ds))

delannoyPathsLayers :: [[[[Int]]]]
delannoyPathsLayers = delannoyPathsHelper [[[]]] [[[2]], [[0]]]

delannoyPaths :: Int -> Int -> [[Int]]
delannoyPaths a b = (last $ take (a + b + 1) $ delannoyPathsLayers) !! b