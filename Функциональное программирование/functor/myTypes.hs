import Prelude hiding (Either, Left, Right)

data Pair a = Pair a a
  deriving (Show)

instance Functor Pair where
  fmap f (Pair a b) = Pair (f a) (f b)

data Labelled e a = Labelled e a
  deriving (Show)

instance Functor (Labelled e) where
  fmap f (Labelled e a) = Labelled e (f a)

data OneOrTwo a = One a | Two a a
  deriving (Show)

instance Functor OneOrTwo where
  fmap f (One a) = One (f a)
  fmap f (Two a b) = Two (f a) (f b)

data Either e a = Left e | Right a
  deriving (Show)

instance Functor (Either e) where
  fmap f (Left e) = Left e
  fmap f (Right a) = Right (f a)

data MultiTree a = Leaf | Node a [MultiTree a]
  deriving (Show)

instance Functor MultiTree where
  fmap f Leaf = Leaf
  fmap f (Node a s) = Node (f a) (fmap (fmap f) s)

data Stream a = Cons a (Stream a)
  deriving (Show)

instance Functor Stream where
  fmap f (Cons a s) = Cons (f a) (fmap f s)
