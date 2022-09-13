using System;
using System.Collections;
using System.Numerics;

namespace lab1
{
    class Program
    {
        public static BigInteger MersennePrime(int p)
        {
            int n = p / 8 + 1;
            byte[] byteArray = new byte[n];
            for (int i = 0; i < n; i++)
            {
                byteArray[i] = 255;
            }

            byteArray[n - 1] = (byte)(byteArray[n - 1] >> (8 - p % 8));

            return new BigInteger(byteArray);
        }

        public static BigInteger RandomPrimeNumber(int length)
        {
            int n = length / 8 + 1;
            Random random = new Random();
            byte[] byteArray = new byte[n];
            random.NextBytes(byteArray);

            byteArray[n - 1] = (byte)(byteArray[n - 1] >> (8 - length % 8));
            byteArray[n - 1] = (byte)(byteArray[n - 1] | (1 << (length % 8 - 1)));
            byteArray[0] = (byte)(byteArray[0] | 1);

            return new BigInteger(byteArray);
        }

        public static bool MRTest(BigInteger prime, double accuracy)
        {
            int nRounds = (int)Math.Ceiling(Math.Log(1 - accuracy, 0.25));

            int s = 0;
            BigInteger r = prime - 1;
            for(; ; s++ ){
                if ((r & 1) == 0)
                {
                    r >>= 1;
                }
                else
                {
                    break;
                }

            }

            Random random = new Random();

            for (int k = 0; k < nRounds; k++)
            {
                byte[] byteArray = prime.ToByteArray();
                random.NextBytes(byteArray);
                byteArray[byteArray.Length - 1] = (byte)(byteArray[byteArray.Length - 1] >> 1);
                BigInteger a = new BigInteger(byteArray) % (prime - 1) + 1;

                if (BigInteger.GreatestCommonDivisor(a, prime) != 1)
                {
                    return false;
                }

                BigInteger u = ModularPow(a, r, prime);

                if (u == 1)
                {
                    continue;
                }

                bool b = false;
                for (int i = 0; i < s; i++)
                {
                    if (u == prime - 1)
                    {
                        b = true;
                        break;
                    }
                    u = (u * u) % prime;
                }
                if (b) { continue; }
                return false;
            }
            return true;
        }

        public static bool SSTest(BigInteger prime, double accuracy)
        {
            int nRounds = (int)Math.Ceiling(Math.Log(1 - accuracy, 0.5));

            BigInteger r = (prime - 1) / 2;

            for (int k = 0; k < nRounds; k++)
            {
                Random random = new Random();
                byte[] byteArray = prime.ToByteArray();
                random.NextBytes(byteArray);
                byteArray[byteArray.Length - 1] = (byte)(byteArray[byteArray.Length - 1] >> 1);
                BigInteger a = new BigInteger(byteArray) % (prime - 2) + 2;

                if (BigInteger.GreatestCommonDivisor(a, prime) != 1)
                {
                    return false;
                }

                BigInteger u = ModularPow(a, r, prime);
                BigInteger j = Jacobi(a, prime);
                
                if (u != (prime + j) % prime)
                {
                    return false;
                }
            }       
            
            return true;
        }

        public static BigInteger ModularPow(BigInteger a, BigInteger b, BigInteger M)
        {
            BigInteger u = 1;
            int length = b.GetByteCount() * 8 - 1;

            for (int i = length; i >= 0; i--)
            {
                u = (u * u) % M;
                if ((b & ((BigInteger)1 << i)) != 0)
                {
                    u = (u * a) % M;
                }
            }

            return u;
        }

        public static BigInteger Jacobi(BigInteger a, BigInteger b)
        {
           if (BigInteger.GreatestCommonDivisor(a, b) != 1)
            {
                return 0;
            }
            int r = 1;
            if (a < 0)
            {
                a = -a;
                if (b % 4 == 3)
                {
                    r = -r;
                }
            }

            while (a != 0)
            {
                int t = 0;
                for (; (a & 1) == 0; t++)
                {
                    a >>= 1;
                }
                if ((t & 1) != 0)
                {
                    if (b % 8 == 3 || b % 8 == 5)
                    {
                        r = -r;
                    }
                }

                if (a % 4 == 3 && b % 4 == 3)
                {
                    r = -r;
                }
                BigInteger c = a;
                a = b % c;
                b = c;
            }
            return r;
        }

        public static bool LLTest(BigInteger prime, int length)
        {
            BigInteger s = 4;
            int k = 1;
            while (k < length - 1) {
                s = (s * s - 2) % prime;
                k++;
            }
            return s == 0;
        }

        static void Main(string[] args)
        {
            Console.Write("Enter number's length (in bits): ");
            int n = Convert.ToInt32(Console.ReadLine());

            BigInteger result = RandomPrimeNumber(n);

            while (true)
            {
                if (MRTest(result, 0.999999) && SSTest(result, 0.999999))
                {
                    break;
                }
                result = RandomPrimeNumber(n);
            }

            Console.WriteLine($"Generated prime: {result}");

            BigInteger result2 = MersennePrime(61);
            Console.WriteLine($"Generated Mersenne prime: {result2}");
            Console.WriteLine($"Lucas–Lehmer test result: {LLTest(result2, 61)}");
        }
    }
}
