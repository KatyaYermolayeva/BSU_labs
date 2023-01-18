import Data.Function
import Prelude hiding (gcd)

gcd :: Int -> Int -> Int
gcdHelper :: (Int -> Int -> Int) -> Int -> Int -> Int
gcdHelper f x y = if x == y then x else (f u v - u)
  where
    u = min x y
    v = max x y
gcd = fix gcdHelper
