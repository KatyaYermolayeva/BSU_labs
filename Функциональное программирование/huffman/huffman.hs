import System.Environment
import Prelude hiding (filter, null)
import System.IO
import Data.Map
import Data.List (sortBy)
import Data.Tree

data Node = Leaf Char Int
          | Interior Node Node Int
            deriving (Show)

fr :: Node -> Int
fr (Leaf c i) = i
fr (Interior a b i) = i

frequencies :: String -> Map Char Int -> [(Char, Int)]
toNodes :: [(Char, Int)] -> [Node]
toTree :: [Node] -> Node
toMap :: Node -> String -> Map Char String
huffman :: String -> Map Char String
encode :: Map Char String -> String -> String
decode :: Map Char String -> String -> String -> String

frequencies [] m = toList m
frequencies (s:ss) m = frequencies ss (insertWith (+) s 1 m)

toNodes a = fmap (\(x, y) -> Leaf x y) a

toTree (n:[]) = n
toTree (a:b:ns) = toTree $ sortBy (\x y -> compare (fr x) (fr y)) ((Interior a b (fr a + fr b)):ns)

toMap (Leaf c i) s = singleton c s
toMap (Interior a b i) s = union (toMap a (s ++ "0")) (toMap b (s ++ "1"))

huffman s = toMap (toTree $ toNodes $ frequencies s empty) ""
		
encode m [] = []
encode m (s:ss) = (m ! s) ++ (encode m ss)

decode m [] [] = []
decode m [] st = 
	let c = filter (\y -> y == st) m 
	in [fst $ elemAt 0 c]
decode m (s:ss) st = 
	let c = filter (\y -> y == st) m 
	in if null c then decode m ss (st ++ [s])
		else [(fst $ elemAt 0 c)] ++ decode m ss [s]	
	

main :: IO ()
main = do
	putStrLn "Enter input file name:"
	a <- getLine
	s <- readFile a
	m <- return $ huffman s
	en <- return $ encode m s
	putStrLn "Enter encoded file name:"
	b <- getLine
	writeFile b en
	de <- return $ decode m en ""
	putStrLn "Enter decoded file name:"
	c <- getLine
	writeFile c de
