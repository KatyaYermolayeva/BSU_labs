namespace lab1
{
    public class Huffman
    {
        class Node
        {
            public int? Letter { get; set; }
            public double Probability { get; set; }
            public Node LeftChild { get; set; }
            public Node RightChild { get; set; }
            public Node Parent { get; set; }
        }

        private double[] alphabetProbabilities;
        private string[] codeWords;

        public Huffman(double[] _alphabetProbabilities)
        {
            alphabetProbabilities = _alphabetProbabilities;
            Array.Sort(alphabetProbabilities);

            codeWords = new string[alphabetProbabilities.Length];
            List<Node> nodes = new List<Node>();
            for (int i = 0; i < alphabetProbabilities.Length; i++)
            {
                nodes.Add(new Node 
                { 
                    Probability = alphabetProbabilities[i],
                    Letter = i
                });
            }

            while(nodes.Count > 1)
            {
                nodes = nodes.OrderBy(n => n.Probability).ToList();
                Node newNode = new Node {
                    Probability = nodes[0].Probability + nodes[1].Probability,
                    RightChild = nodes[0],
                    LeftChild = nodes[1]
                };
                nodes[0].Parent = newNode;
                nodes[1].Parent = newNode;
                nodes.RemoveAt(0);
                nodes.RemoveAt(0);
                nodes.Add(newNode);
            }

            Node node = nodes[0];
            string word = "";
            while (true)
            {
                if (node.Letter != null)
                {
                    codeWords[(int)node.Letter] = word;
                    word = word.Substring(0, word.Length - 1);
                    node = node.Parent;
                }
                else if (node.LeftChild != null)
                {
                    word += "1";
                    node = node.LeftChild;
                    node.Parent.LeftChild = null;
                }
                else if (node.RightChild != null)
                {
                    word += "0";
                    node = node.RightChild;
                    node.Parent.RightChild = null;
                }
                else if (node.Parent != null)
                {
                    word = word.Substring(0, word.Length - 1);
                    node = node.Parent;
                }
                else break;
            }

        }

        public double GetAverageWordLength()
        {
            double result = 0;
            for (int i = 0; i < codeWords.Length; i++)
            {
                result += codeWords[i].Length * alphabetProbabilities[i];
            }
            return result;
        }

        public double GetEntropy()
        {
            double result = 0;
            for (int i = 0; i < codeWords.Length; i++)
            {
                result += Math.Log2(alphabetProbabilities[i]) * alphabetProbabilities[i];
            }
            return -result;
        }

        public void PrintWords()
        {
            for (int i = 0; i < codeWords.Length; i++)
            {
                Console.WriteLine(codeWords[i] + " for symbol with probability " + alphabetProbabilities[i]);
            }
        }
    }

    class Program
    {
        static void Main(string[] args)
        {
            double[] p = new double[] { 0.01, 0.01, 0.01, 0.01, 0.01, 0.95 };
            Huffman h = new Huffman(p);
            h.PrintWords();
            Console.WriteLine(h.GetAverageWordLength());
            Console.WriteLine(h.GetEntropy());
        }
    }
}
