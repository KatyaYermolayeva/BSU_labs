namespace lab2
{
    internal class Program
    {
        public static List<string> GenerateSequences(int length)
        {
            List<string> sequences = new List<string>();
            for (int i = 0; i < Math.Pow(2, length); i++)
            {
                string sequence = Convert.ToString(i, 2).PadLeft(length, '0');
                sequences.Add(sequence);
            }
            return sequences;
        }

        public static double f(string sequence)
        {
            double result = 1;
            for (int i = 0; i < sequence.Length; i++)
            {
                if (sequence[i] == '0')
                {
                    result *= 1.0 / 3;
                }
                else
                {
                    result *= 2.0 / 3;
                }
            }
            return result;
        }

        public static List<string> GetTypicalSequences(List<string> sequences, double eps, double H, int n)
        {
            List<string> typicalSequences = new List<string>();
            foreach (var sequence in sequences)
            {
                if (Math.Abs(-H - Math.Log2(f(sequence)) / n) <= eps)
                {
                    typicalSequences.Add(sequence);
                }
            }
            return typicalSequences;
        }

        public static (double, double) GetTypicalSequencesCountAndSumProbability(int length, double eps, double p)
        {
            double eps1 = eps / Math.Log2((1 - p) / p);
            double count = 0;
            double sum = 0;
            for (int i = 0; i <= length; i++)
            {
                if (Math.Abs(((double)i / length) - p) <= eps1)
                {
                    double k = 1.0;
                    for (int j = 1; j <= i; j++)
                    {
                        k *= (double)(length - j + 1) / j;
                        
                    }
                    sum += k * Math.Pow((1 - p), (length - i)) * Math.Pow(p, i);
                    count += k;

                }
                if ((double)i / length - p > eps1)
                {
                    break;
                }
            }
            return (count, sum);
        }

        static void Main(string[] args)
        {
            int n = 5;
            double eps = 0.138;
            double H = -Math.Log2(1.0 / 3) / 3 - 2 * Math.Log2(2.0 / 3) / 3;
            double lbound = Math.Pow(2, -n * (H + eps));
            double ubound = Math.Pow(2, -n * (H - eps));
            List<string> sequences = GenerateSequences(n);
            List<string> typicalSequences = GetTypicalSequences(sequences, eps, H, n);
            double P = 0;
            foreach (var sequence in typicalSequences)
            {
                P += f(sequence);
            }

            Console.WriteLine("Пусть 0 - вытягивание черного шарика, 1 - вытягивание белого шарика");
            Console.WriteLine($"Энтропия:\nH(X) = {H}");
            Console.WriteLine($"Нижняя граница: {lbound}");
            Console.WriteLine($"Верхняя граница: {ubound}");

            Console.WriteLine($"Все последовательности:");
            foreach (var sequnce in sequences)
            {
                Console.WriteLine(sequnce);
            }

            Console.WriteLine($"\nE-типичные последовательности для е = 0.138:");
            foreach (var sequnce in typicalSequences)
            {
                Console.WriteLine(sequnce);
            }

            Console.WriteLine($"Количество е-типичных последовательностей: {typicalSequences.Count}");
            Console.WriteLine($"Доля е-типичных последовательностей: {(double)typicalSequences.Count / sequences.Count}");
            Console.WriteLine($"Суммарная вероятность: {P}");

            double a = (1 - eps) / ubound;
            double b = 1 / lbound;

            if (typicalSequences.Count >= a && typicalSequences.Count <= b)
            {
                Console.WriteLine($"{a} <= {typicalSequences.Count} <= {b} - неравенство выполняется");
            }
            else
            {
                Console.WriteLine($"{a} <= {typicalSequences.Count} <= {b} - неравенство не выполняется");
            }

            int[] ns = new int[] { 100, 10, 100, 500, 1000, 2000 };
            double[] es = new double[] { 0.138, 0.1, 0.2, 0.3, 0.4};

            for (int i = 0; i < ns.Length; i++)
            {
                n = ns[i];
                double sc = Math.Pow(2, n);
                Console.WriteLine($"\nn = {n}");
                Console.WriteLine($"Количество последовательностей: {sc}");

                for (int j = 0; j < es.Length; j++)
                {
                    eps = es[j];
                    Console.WriteLine($"\ne = {eps}");
                    lbound = Math.Pow(2, -n * (H + eps));
                    ubound = Math.Pow(2, -n * (H - eps));
                    (double tsc, P) = GetTypicalSequencesCountAndSumProbability(n, eps, 1.0 / 3);
                    Console.WriteLine($"Нижняя граница: {lbound}");
                    Console.WriteLine($"Верхняя граница: {ubound}");
                    Console.WriteLine($"Количество типичных последовательностей: {tsc}");
                    Console.WriteLine($"Доля типичных последовательностей: {tsc / sc}");
                    Console.WriteLine($"Суммарная вероятность: {P}");
                    a = (1 - eps) / ubound;
                    b = 1 / lbound;
                    if (tsc >= a && tsc <= b)
                    {
                        Console.WriteLine($"{a} <= {tsc} <= {b} - неравенство выполняется");
                    }
                    else
                    {
                        Console.WriteLine($"{a} <= {tsc} <= {b} - неравенство не выполняется");
                    }
                }
            }

            eps = 0.138;
            for (int i = 5; i < 100; i++)
            {
                double count;
                (count, P) = GetTypicalSequencesCountAndSumProbability(i, eps, 1.0 / 3);
                if (P > 1 - eps)
                {
                    Console.WriteLine($"\n Неравенство выполняется для e = {eps} при n = {i}");
                    Console.WriteLine($"Количество типичных последовательностей - {count}, суммарная вероятность - {P}");
                    break;
                }
            }
        }
    }
}