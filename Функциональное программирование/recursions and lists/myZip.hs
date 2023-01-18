xZipWith :: (a -> b -> c) -> [a] -> [b] -> [c]
xZipWith _ [] _ = []
xZipWith _ _ [] = [] 
xZipWith f (a:as) (b:bs) = f a b : xZipWith f as bs