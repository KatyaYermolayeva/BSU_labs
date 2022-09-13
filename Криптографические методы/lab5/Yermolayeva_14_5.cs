using System;
using System.Collections;
using System.Numerics;

namespace lab5
{
    class RSA
    {
        BigInteger p;
        BigInteger q;
        BigInteger e;
        BigInteger d;
        BigInteger n;
        BigInteger fn;

        public RSA(BigInteger _p, BigInteger _q, BigInteger _e)
        {
            p = _p;
            q = _q;
            e = _e;

            try
            {
                KeyGen();
            }
            catch (ArgumentException)
            {
                throw new ArgumentException("Invalid parameters");
            }
        }

        void KeyGen()
        {
            n = BigInteger.Multiply(p, q);
            fn = BigInteger.Multiply(p - 1, q - 1);
            BigInteger buf = new BigInteger();

            if (GCD(e, fn, ref d, ref buf) != 1)
            {
                throw new ArgumentException();
            }

            if (d < 0)
            {
                d += fn;
            }
        }

        BigInteger GCD(BigInteger a, BigInteger b, ref BigInteger x, ref BigInteger y)
        {
            if (a == 0)
            {
                x = 0; y = 1;
                return b;
            }
            BigInteger x1 = new BigInteger(), y1 = new BigInteger();
            BigInteger d = GCD(b % a, a, ref x1, ref y1);
            x = y1 - (b / a) * x1;
            y = x1;
            return d;
        }

        BigInteger GCD(BigInteger a, BigInteger b)
        {
            if (a == 0)
            {
                return b;
            }
            BigInteger d = GCD(b % a, a);
            return d;
        }

        public BigInteger Encr(BigInteger X)
        {
            return ModularPow(X, e, n);
        }

        public BigInteger Decr(BigInteger Y)
        {
            return ModularPow(Y, d, n);
        }

        BigInteger ModularPow(BigInteger a, BigInteger b, BigInteger M)
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

        public void PrintParams()
        {
            Console.WriteLine($"p = {p}\nq = {q}\nn = {n}\nfn = {fn}\ne = {e}\nd = {d}\n");
        }
    }

    class Program
    {
        static BigInteger GCD(BigInteger a, BigInteger b)
        {
            if (a == 0)
            {
                return b;
            }
            BigInteger d = GCD(b % a, a);
            return d;
        }

        static BigInteger[] Factorization(BigInteger a)
        {
            BigInteger[] result = new BigInteger[2];
            byte[] byteArray = a.ToByteArray();
            Random random = new Random();

            random.NextBytes(byteArray);
            BigInteger x = 2;
            BigInteger y = 1; 
            int i = 0; 
            int stage = 2;

            BigInteger nod = GCD(a, BigInteger.Abs(x - y));
            while (nod == 1)
            {
                if (i == stage)
                {
                    y = x;
                    stage *= 2;
                }
                x = (x * x + 1) % a;
                i++;
                nod = GCD(a, BigInteger.Abs(x - y));
            }

            result[0] = nod;
            result[1] = a / result[0];
            return result;
        }

        static void Main(string[] args)
        {
            BigInteger p = 684391453787369;
            BigInteger q = 938396705691661;
            BigInteger e = BigInteger.Parse("245372344253915653531369256899");

            RSA rsa = new RSA(p, q, e);
            rsa.PrintParams();

            BigInteger X1 = BigInteger.Parse("184712154522842417799563173273");
            BigInteger Y1 = rsa.Encr(X1);
            BigInteger decrX1 = rsa.Decr(Y1);

            Console.WriteLine($"X1 = {X1}\nY1 = Encr(X1) = {Y1}\nDecr(Y1) = {decrX1}\n");

            BigInteger Y2 = BigInteger.Parse("447204864183801463638208868116");
            BigInteger X2 = rsa.Decr(Y2);
            Console.WriteLine($"Y2 = {Y2}\nX2 = Decr(Y2) = {X2}\n");

            BigInteger n = BigInteger.Multiply(p, q);
            BigInteger[] pq = Factorization(n);
            Console.WriteLine($"p = {pq[0]}\nq = {pq[1]}");

        }
    }
}
