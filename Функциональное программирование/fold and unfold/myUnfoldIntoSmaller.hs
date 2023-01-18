import Data.List (unfoldr)

myUnfoldIntoSmallerHelper 0 = Nothing
myUnfoldIntoSmallerHelper n = Just (n, n - 1)

myUnfoldIntoSmaller n = unfoldr myUnfoldIntoSmallerHelper n