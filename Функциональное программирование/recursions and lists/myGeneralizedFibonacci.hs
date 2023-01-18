generalizedFibonacci :: [Int] -> [Int]
generalizedFibonacci [] = []
generalizedFibonacci (a : as) = a : generalizedFibonacci (as ++ [sum (a : as)])
