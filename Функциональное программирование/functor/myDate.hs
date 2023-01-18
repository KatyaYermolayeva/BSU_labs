import Data.Char (isDigit)
import Data.Map

monthDays = fromList [
    (1, 31),
    (2, 28),
    (3, 31),
    (4, 30),
    (5, 31),
    (6, 30),
    (7, 31),
    (8, 31),
    (9, 30),
    (10, 31),
    (11, 30),
    (12, 31)
  ]

leapMonthDays = fromList [
    (1, 31),
    (2, 29),
    (3, 31),
    (4, 30),
    (5, 31),
    (6, 30),
    (7, 31),
    (8, 31),
    (9, 30),
    (10, 31),
    (11, 30),
    (12, 31)
  ]

isLeap x = (x `mod` 4 == 0) && ((x `mod` 400 == 0) || (x `mod` 100 /= 0))

data Date = Date {
  d :: Int,
  m :: Int,
  y :: Int
}

instance Eq Date where
  Date d1 m1 y1 == Date d2 m2 y2 = (d1 == d2) && (m1 == m2) && (y1 == y2)

instance Ord Date where
  (Date d1 m1 y1) < (Date d2 m2 y2) = (y1 < y2) || ((y1 == y2) && (m1 < m2)) || ((y1 == y2) && (m1 == m2) && (d1 < d2))
  d1 <= d2 = (d1 == d2) || (d1 < d2)

p s n = replicate (n - length s) '0' ++ s

instance Show Date where
  show (Date d m y) = 
          let sd = show d
              sm = show m
              sy = show y
          in p sd 2 ++ "." ++ p sm 2 ++ "." ++ p sy 4

instance Read Date where
  readsPrec _ (d1:d2:'.':m1:m2:'.':y1:y2:y3:y4:therest) = 
        let d = read [d1,d2] :: Int
            m = read [m1,m2] :: Int
            y = read [y1, y2, y3, y4] :: Int
        in if all isDigit [d1,d2,m1,m2,y1,y2,y3,y4] then [(Date d m y,therest)]
          else []                      
  readsPrec _ _ = []              

add :: Date -> Int -> Date
add d 0 = d
add (Date d m y) x = 
  let dd 
         | isLeap y = monthDays ! m
         | otherwise = leapMonthDays ! m
      newD 
         | (d == dd) && (m == 12) = Date 1 1 (y + 1)
         | d == dd = Date 1 (m + 1) y
         | otherwise = Date (d + 1) m y
  in add newD (x - 1)  

subHelper :: Date -> Date -> Int -> Int
subHelper d1 d2 s 
   |  d1 == d2 = s
   | otherwise = subHelper d1 (add d2 1) (s + 1)
sub :: Date -> Date -> Int
sub d1 d2 
   | d1 < d2 = - subHelper d2 d1 0
   | otherwise = subHelper d1 d2 0 