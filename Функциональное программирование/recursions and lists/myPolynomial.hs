evalPolynomialHelper :: [Int] -> Int -> Int -> Int -> Int
evalPolynomialHelper [] b c d = d
evalPolynomialHelper (a:as) b c d = evalPolynomialHelper as b (c * b) (d + a * c) 
evalPolynomial :: [Int] -> Int -> Int
evalPolynomial a b = evalPolynomialHelper (reverse a) b 1 0
