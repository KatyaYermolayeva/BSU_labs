using System;
using System.Collections;
using System.IO;

namespace lab3
{
    class Program
    {
        static BitArray LFSR(int N, int n, BitArray s, BitArray a)
        {
            BitArray result = new BitArray(N);
            for (int i = 0; i < n; i++)
            {
                result[i] = s[i];
            }
            for (int i = 0; i < N - n; i++)
            {
                bool buffer = false; 
                for (int j = 0; j < n; j++)
                {
                    buffer ^= a[j] & result[i + j];
                }
                result[i + n] = buffer;
            }
            return result;

        }
        static BitArray GeffeGenerator(int N, BitArray LFSR1, BitArray LFSR2, BitArray LFSR3)
        {
            BitArray result = new BitArray(N);
            for (int i = 0; i < N; i++)
            {
                result[i] = (LFSR1[i] & LFSR2[i]) ^ ((LFSR1[i] ^ true) & LFSR3[i]);
            }
            return result;
        }

        static int GetSequencePeriod(BitArray sequence, int n)
        {
            int period = 0;
            int j = 0;
            for (int i = n; i < sequence.Length; i++)
            {
                if (sequence[i] == sequence[j])
                {
                    j++;
                }
                else
                {
                    j = 0;
                    if (sequence[i] == sequence[j])
                    {
                        j++;
                    }
                }

                if (j == n)
                {
                    period = i + 1 - n;
                    break;
                }
            }
            return period;
        }

        static BitArray BerlekampMassey(BitArray sequence)
        {
            int n = sequence.Length;
            int N = 0, L = 0, m = -1;
            BitArray b = new BitArray(n);
            BitArray c = new BitArray(n);
            BitArray t = new BitArray(n);
            b[0] = true;
            c[0] = true;
            bool d;

            while(N < n)
            {
                d = sequence[N];
                for (int i = 1; i <= L; i++)
                {
                    d ^= (sequence[N - i] & c[i]);
                }
                if (d)
                {
                    t = (BitArray)c.Clone();
                    for (int i = 0; i < n - N + m; i++)
                    {
                        c[N - m + i] = (c[N - m + i] ^ b[i]);
                    }
                    if (2 * L <= N)
                    {
                        L = N + 1 - L;
                        m = N;
                        b = (BitArray)t.Clone();
                    }
                }
                N++;
            }
            BitArray result = new BitArray(L);
            for (int i = 1; i <= L; i++)
            {
                result[i - 1] = c[i];
            }
            return result;
        }

        static void Main(string[] args)
        {
            int N = 10000;
            int N2 = 10000000;

            BitArray s1 = new BitArray(new bool[] { false, true, false, false, true});
            BitArray s2 = new BitArray(new bool[] { false, false, true, true, true, false, false });
            BitArray s3 = new BitArray(new bool[] { true, false, false, false, true, false, true, true });

            BitArray a1 = new BitArray(new bool[] { true, true, true, true, false });
            BitArray a2 = new BitArray(new bool[] { true, true, true, false, false, true, false });
            BitArray a3 = new BitArray(new bool[] { true, false, true, true, true, false, false, false });

            BitArray LFSR1 = LFSR(N, 5, s1, a1);
            BitArray LFSR2 = LFSR(N, 7, s2, a2);
            BitArray LFSR3 = LFSR(N, 8, s3, a3);

            Console.WriteLine($"Период генератора 1: {GetSequencePeriod(LFSR1, 5)}");
            Console.WriteLine($"Период генератора 2: {GetSequencePeriod(LFSR2, 7)}");
            Console.WriteLine($"Период генератора 3: {GetSequencePeriod(LFSR3, 8)}");

            BitArray GeffeResult = GeffeGenerator(N, LFSR1, LFSR2, LFSR3);

            int count1 = 0;
            for (int i = 0; i < N; i++)
            {
                if (GeffeResult[i])
                {
                    count1++;
                }
            }

            int[] r = new int[5];
            for (int i = 0; i < r.Length; i++)
            {
                for (int j = 0; j < N - i - 1; j++)
                {
                    if (GeffeResult[j] ^ GeffeResult[j + i + 1])
                    {
                        r[i]--;
                    }
                    else
                    {
                        r[i]++;
                    }
                }
            }

            Console.WriteLine($"Количество 0: {N - count1}\nКоличество единиц: {count1}");
            for (int i = 0; i < r.Length; i++)
            {
                Console.WriteLine($"r{i + 1} = {r[i]}");
            }

            LFSR1 = LFSR(N2, 5, s1, a1);
            LFSR2 = LFSR(N2, 7, s2, a2);
            LFSR3 = LFSR(N2, 8, s3, a3);

            GeffeResult = GeffeGenerator(N2, LFSR1, LFSR2, LFSR3);

            string path = @".\geffe.dat";
            using (BinaryWriter writer = new BinaryWriter(File.Open(path, FileMode.OpenOrCreate)))
            {
                byte[] bytes = new byte[GeffeResult.Length / 8 + (GeffeResult.Length % 8 == 0 ? 0 : 1)];
                GeffeResult.CopyTo(bytes, 0);
                foreach (var b in bytes)
                {
                    writer.Write(b);
                }
            }

            BitArray a1Test = BerlekampMassey(LFSR1);
            BitArray a2Test = BerlekampMassey(LFSR2);
            BitArray a3Test = BerlekampMassey(LFSR3);

            Console.WriteLine($"\nПорядок многочлена: {a1Test.Length}");
            Console.WriteLine("Коэффициенты:");

            for (int i = 0; i < a1Test.Length; i++)
            {
                Console.Write((a1Test[i] ? "1" : "0") + " ");
            }

            Console.WriteLine($"\nПорядок многочлена: {a2Test.Length}");
            Console.WriteLine("Коэффициенты:");

            for (int i = 0; i < a2Test.Length; i++)
            {
                Console.Write((a2Test[i] ? "1" : "0") + " ");
            }

            Console.WriteLine($"\nПорядок многочлена: {a3Test.Length}");
            Console.WriteLine("Коэффициенты:");

            for (int i = 0; i < a3Test.Length; i++)
            {
                Console.Write((a3Test[i] ? "1" : "0") + " ");
            }

        }
    }
}
