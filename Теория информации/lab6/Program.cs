namespace lab6
{
    public class LempelZiv
    {
        public List<(int, char)> Encode(string text)
        {
            text += "\0";
            List <string> phrases = new List <string>();
            List<(int, char)> result = new List<(int, char)> ();
            for (int i = 0; i < text.Length; i++) 
            {
                int res = 0;
                string phrase = "" + text[i];
                int id = phrases.IndexOf(phrase);
                while (id != -1 && i < text.Length - 1)
                {
                    i++;
                    phrase += text[i];
                    res = id;
                    id = phrases.IndexOf(phrase);
                }
                phrases.Add(phrase);
                result.Add((res, phrase[phrase.Length - 1]));
            }
            return result;
        }
    }

    class Program
    {
        static void Main(string[] args)
        {
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

                LempelZiv l = new LempelZiv();
                var result = l.Encode(text);
                double cn = result.Count;
                int n = text.Length;
               // double e = 1 - (Math.Log2(Math.Log2(n) + 2) + 3) / Math.Log2(n);
                double e = 0.99;
                double cne = n / Math.Log2(n) / e;
                Console.WriteLine($"\nНеравенство Лемпеля-Зива - {cn} <= {cne}\n");
            }
        }
    }
}
