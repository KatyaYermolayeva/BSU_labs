namespace lab5
{
    public class RyabkoElias
    {
        private List<String> phrases;

        public RyabkoElias(char[] alphabet, int k)
        {
            phrases = new List<String>();
            for (int i = 0; i < Math.Pow(alphabet.Length, k); i++)
            {
                int id = i;
                string phrase = "";

                for (int j = 0; j < k; j++)
                {
                    phrase += alphabet[id % alphabet.Length];
                    id /= alphabet.Length;
                }
                phrases.Add(phrase);
            }
        }

        public string Encode(string phrase)
        {
            int i = phrases.IndexOf(phrase);
            phrases.Remove(phrase);
            phrases.Insert(0, phrase);
            return Elias(i);
        }

        public string Elias (int n)
        {
            string end = Convert.ToString(n, 2);
            string length = Convert.ToString(end.Length, 2);
            length = new string ('0', length.Length - 1) + length;
            end = end.Substring(1);
            return length + end;
        }
    }

    class Program
    {
        static void Main(string[] args)
        {
            int[] ks = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

            char[] alphabet = new char[] {
                'ф', 'э', 'щ', 'ц', 'ш', 'ю', 'ж', 'х', 'й', 'ч', 'г', 'б', 'ь', 'з', 'ы',
                'я', 'у', 'п', 'д', 'м', 'к', 'л', 'в', 'р', 'с', 'н', 'т', 'и', 'а', 'е', 'о', ' '
            };
            char[] bAlphabet = new char[] {
                '0', '1'
            };


            using (StreamReader file = new StreamReader("text1.txt"))
            {
                string text = file.ReadToEnd();
                file.Close();
                text = string.Join("", System.Text.Encoding.Default.GetBytes(text).Select(n => Convert.ToString(n, 2)));

                for (int i = 0; i < ks.Length; i++)
                {
                    int k = ks[i];
                    RyabkoElias r = new RyabkoElias(bAlphabet, k);
                    Console.WriteLine($"k = {k}\n");
                    double avg = 0.0;
                    int j = 0;
                    char[][] blocks = text.GroupBy(s => j++ / k).Select(s => s.ToArray()).ToArray();
                    Array.Resize(ref blocks[blocks.Length - 1], k);
                    foreach (char[] block in blocks)
                    {
                        string b = new string(block);
                        string code = r.Encode(b);
                        avg += code.Length;
                    }
                    avg /= blocks.Length;
                    Console.WriteLine($"\nСредняя длина кодового слова - {avg}\n");
                }
            }
        }
    }
}
