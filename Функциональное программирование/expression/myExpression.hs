import Data.Map

type VarBinding a = Map String a

data Expr a u b
    = Const a
    | Var String
    | UnOp u (Expr a u b)
    | BinOp b (Expr a u b) (Expr a u b)
    deriving (Show)

data IntUnOp
    = IntNeg
    | IntInc
    | IntDec
    deriving (Show, Eq, Ord)

data IntBinOp
    = IntAdd
    | IntSub
    | IntMul
    | IntDiv
    | IntMod
    deriving (Show, Eq, Ord)

type UnOpSemantic a u = Map u (a -> a)
type BinOpSemantic a b = Map b (a -> a -> a)

intUnOpSemantic :: UnOpSemantic Int IntUnOp
intUnOpSemantic = fromList [
    (IntNeg, negate),
    (IntInc, (+) 1),
    (IntDec, (flip (-)) 1)
  ]

intBinOpSemantic :: BinOpSemantic Int IntBinOp
intBinOpSemantic = fromList [
    (IntAdd, (+)),
    (IntSub, (-)),
    (IntMul, (*)),
    (IntDiv, div),
    (IntMod, mod)
  ]

eval
  :: (Ord u, Ord b)
  => BinOpSemantic a b
  -> UnOpSemantic a u
  -> VarBinding a
  -> Expr a u b
  -> a
eval _ _ _ (Const x) = x
eval _ _ m (Var s) = m ! s
eval b u m (UnOp f e) = (u ! f) $ (eval b u m e)
eval b u m (BinOp f e1 e2) = (b ! f) (eval b u m e1) (eval b u m e2)

tree :: Expr Int IntUnOp IntBinOp
tree = BinOp IntAdd
    (UnOp IntNeg (BinOp IntDiv (Const 3) (Var "k")))
    (Var "k")

intUnOpToString = fromList [
    (IntNeg, "~"),
    (IntInc, "inc"),
    (IntDec, "dec")
  ]
intBinOpToString = fromList [
    (IntAdd, "+"),
    (IntSub, "-"),
    (IntMul, "*"),
    (IntDiv, "/"),
    (IntMod, "%")
  ]

toString :: (Show a)
  => (b -> String) -> (u -> String) -> Expr a u b -> String
toString _ _ (Const x) = show x
toString _ _ (Var s) = s
toString b u (BinOp f e1 e2) = "(" ++ (toString b u e1) ++ " " ++ (b f) ++ " " ++ (toString b u e2) ++ ")"
toString b u (UnOp f e) = (u f) ++ "(" ++ (toString b u e) ++ ")"

