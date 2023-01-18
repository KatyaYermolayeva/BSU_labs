data MyTree a
  = XNil
  | XCons a (MyTree a) (MyTree a)

xMap :: (a -> b) -> MyTree a -> MyTree b
xMap _ XNil = XNil
xMap f (XCons x a b) = XCons (f x) (xMap f a) (xMap f b)

xSize :: MyTree a -> Int
xSize XNil = 0
xSize (XCons x a b) = 1 + xSize a + xSize b

treeTraverseD :: (a -> b -> b) -> b -> MyTree a -> b
treeTraverseD _ b XNil = b
treeTraverseD f b (XCons x left right) = treeTraverseD f (f x (treeTraverseD f b left)) right

treeTraverseWHelper :: (a -> b -> b) -> b -> [MyTree a] -> b
treeTraverseWHelper _ b [] = b
treeTraverseWHelper f b (XNil : trees) = treeTraverseWHelper f b trees
treeTraverseWHelper f b ((XCons a left right) : trees) = treeTraverseWHelper f (f a b) (trees ++ [left, right])

treeTraverseW :: (a -> b -> b) -> b -> MyTree a -> b
treeTraverseW f b x = treeTraverseWHelper f b [x]