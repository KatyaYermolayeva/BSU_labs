namespace lab3
{
    public class Tunstall
    {
        class Node
        {
            public int? Letter { get; set; }
            public double Probability { get; set; }
            public Node Parent { get; set; }
        }

        private double[] alphabetProbabilities;
        private string[] codeWords;
        private int blockLength;
        private int d;
        private double avLength = 0;

        public Tunstall(double[] _alphabetProbabilities, int _blockLength, int _d)
        {
            blockLength = _blockLength;
            alphabetProbabilities = _alphabetProbabilities;
            d = _d;

            int L = alphabetProbabilities.Length;
            int q = (int)Math.Floor((Math.Pow(d, blockLength) - L) / (L - 1));
            List<Node> nodes = new List<Node>();
            Node root = new Node()
            {
                Probability = 1
            };
            nodes.Add(root);
            codeWords = new string[L + q  * (L - 1)];

            for (int i = 0; i <= q; i++)
            {
                nodes = nodes.OrderByDescending(n => n.Probability).ToList();
                Node curNode = nodes.First();
                double p = curNode.Probability;
                avLength += p;
                for (int j = 0; j < L; j++)
                {
                    Node newNode = new Node() {                     
                        Probability = p * alphabetProbabilities[j],
                        Parent = curNode,
                        Letter = j
                    };
                    nodes.Add(newNode);
                }
                nodes.Remove(curNode);
            }

            for (int i = 0; i < nodes.Count; i++)
            {
                Node curNode = nodes.ElementAt(i);
                string codeWord = "";
                while (curNode.Letter != null)
                {
                    codeWord = curNode.Letter + codeWord;
                    curNode = curNode.Parent;
                }
                codeWords[i] = codeWord;
            }

        }
        
        private double f(string word)
        {
            double result = 1.0;
            for (int i = 0; i < word.Length; i++)
            {
                result *= alphabetProbabilities[Int32.Parse(word[i].ToString())];
            }
            return result;
        }

        public double GetAverageSymbolLength()
        {
            double result = 0;
            for (int i = 0; i < codeWords.Length; i++)
            {
                string word = Convert.ToString(i, 2).PadLeft(blockLength, '0');
                result += codeWords[i].Length * f(word);
            }
            return result;
        }

        public double GetAverageWordLength()
        {           
            return avLength;
        }

        public double GetAverageCodeSymbolNumber()
        {
            return blockLength / GetAverageWordLength();
        }

        public double GetEntropy()
        {
            double result = 0;
            for (int i = 0; i < alphabetProbabilities.Length; i++)
            {
                result += Math.Log2(alphabetProbabilities[i]) * alphabetProbabilities[i];
            }
            return -result;
        }


        public double GetLowerBound()
        {
            return GetEntropy() / Math.Log2(d);
        }

        public void PrintWords()
        {
            for (int i = 0; i < codeWords.Length; i++)
            {
                string word = Convert.ToString(i, 2).PadLeft(blockLength, '0');
                Console.WriteLine(codeWords[i] + " as " + word);
            }
        }
    }

    class Program
    {
        static void Main(string[] args)
        {
            int n = 3;
            double[] p = new double[] { 0.6, 0.4 };
            Tunstall t = new Tunstall(p, n, p.Length);
            t.PrintWords();
            Console.WriteLine(t.GetAverageWordLength());
            Console.WriteLine(t.GetAverageCodeSymbolNumber());
            Console.WriteLine(t.GetLowerBound());
        }
    }
}
