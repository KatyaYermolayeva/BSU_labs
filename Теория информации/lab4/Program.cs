namespace lab4
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

        public string GetCodeWord(int i)
        {
            return codeWords[i];
        }

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

            while (nodes.Count > 1)
            {
                nodes = nodes.OrderBy(n => n.Probability).ToList();
                Node newNode = new Node
                {
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
        static Dictionary<string, double> GetFrequences(string text, int n)
        {
            Dictionary<string, double> frequences = new Dictionary<string, double>();
            for (int i = 0; i < text.Length; i += n)
            {
                string sub = text.Substring(i, Math.Min(n, text.Length - i));
                if (frequences.ContainsKey(sub))
                {
                    frequences[sub] = frequences[sub] + 1.0;
                }
                else
                {
                    frequences.Add(sub, 1.0);
                }
            }

            foreach (var f in frequences)
            {
                frequences[f.Key] = n * frequences[f.Key] / text.Length;
            }

            return frequences;
        }

        static void Main(string[] args)
        {
            char[] alphabet = new char[] {
                'ф', 'э', 'щ', 'ц', 'ш', 'ю', 'ж', 'х', 'й', 'ч', 'г', 'б', 'ь', 'з', 'ы',
                'я', 'у', 'п', 'д', 'м', 'к', 'л', 'в', 'р', 'с', 'н', 'т', 'и', 'а', 'е', 'о', ' '
            };
            double[] p = new double[] {
                0.002, 0.003, 0.003, 0.004, 0.006, 0.006, 0.007, 0.009, 0.01, 0.012, 0.013, 0.014, 0.014, 0.016, 0.016, 0.021,
                0.018, 0.023, 0.025, 0.026, 0.028, 0.035, 0.038, 0.04, 0.045, 0.053, 0.053, 0.062, 0.062, 0.072, 0.09, 0.175
            };
            Huffman h = new Huffman(p);
            h.PrintWords();

            Console.WriteLine($"\nСредняя длина кодового слова - {h.GetAverageWordLength()}");
            Console.WriteLine($"Энтропия - {h.GetEntropy()}");

            using (StreamReader file = new StreamReader("text.txt"))
            {
                string text = file.ReadToEnd();
                file.Close();
                string hText = "";
                foreach (var c in text)
                {
                    hText += h.GetCodeWord(Array.IndexOf(alphabet, c));
                }

                Console.WriteLine($"\nИсходный текст:\n{text}");
                Console.WriteLine($"\nЗакодированный текст:\n{hText}");
            }

            using (StreamReader file = new StreamReader("text1.txt"))
            {
                string text = file.ReadToEnd();
                file.Close();

                var frequences = GetFrequences(text, 1);

                frequences.OrderBy(f => f.Value);
                string[] a = frequences.Keys.ToArray();
                p = frequences.Values.ToArray();

                h = new Huffman(p);

                Console.WriteLine($"\nСредняя длина кодового слова - {h.GetAverageWordLength()}");
                Console.WriteLine($"Энтропия - {h.GetEntropy()}");
            }

            using (StreamReader file = new StreamReader("text2.txt"))
            {
                string text = file.ReadToEnd();
                file.Close();

                var frequences = GetFrequences(text, 2);

                frequences.OrderBy(f => f.Value);
                string[] a = frequences.Keys.ToArray();
                p = frequences.Values.ToArray();

                h = new Huffman(p);

                Console.WriteLine($"\nСредняя длина кодового слова - {h.GetAverageWordLength()}");
                Console.WriteLine($"Энтропия - {h.GetEntropy()}");
            }
        }
    }
}
