type Stack a = [a]

data Command a
  = StUnOp (a -> a)
  | StBinOp (a -> a -> a)
  | StSpecial Int ([a] -> [a])

do_command :: Command a -> Stack a -> Stack a
do_command (StUnOp f) (x : ts) = (f x) : ts
do_command (StBinOp f) (x : (y : ts)) = (f y x) : ts
do_command (StSpecial n f) ts = (f (take n ts)) ++ (drop n ts)

type Program a = [Command a]

run :: Program a -> Stack a -> Stack a
run [] = id
run (c : cs) = (run cs) . (do_command c)

st_add = StBinOp (+)

st_sub = StBinOp (-)

st_mul = StBinOp (*)

st_div = StBinOp div

st_mod = StBinOp mod

st_push x = StSpecial 0 (const [x])

st_neg = StUnOp negate

st_swap = StSpecial 2 (\(x : (y : [])) -> [y, x])

st_discard = StSpecial 1 (const [])

st_dupl = StSpecial 1 (\(x : []) -> [x, x])

--st_add (x:(y:ts)) = (y + x): ts
--st_sub (x:(y:ts)) = (y - x): ts
--st_mul (x:(y:ts)) = (y * x): ts
--st_div (x:(y:ts)) = (y `div` x): ts
--st_mod (x:(y:ts)) = (y `mod` x): ts

--st_push x = (x:)
--st_neg (x:ts) = (negate x): ts
--st_swap (x:(y:ts)) = y:(x:ts)
--st_discard (x: ts) = ts
--st_dupl (x:ts) = x: (x:ts)

st_wipe (x : ts) = []

st_mess = map (+ 10)

the_program = [st_push 3, st_add, st_dupl, st_mul, st_push 10, st_sub]
