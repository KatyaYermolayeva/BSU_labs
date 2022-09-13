using System;

namespace Lab_4
{
    public class NistTests
    {
        public static void NistBattery(bool[] sequence, double firstError)
        {
            if (MonobitsTest(sequence, firstError))
                Console.WriteLine("Частотный побитовый тест пройден.");
            else
                Console.WriteLine("Частотный побитовый тест провален.");
            if (RunsTest(sequence, firstError))
                Console.WriteLine("Тест на одинаковые идущие подряд биты пройден.");
            else
                Console.WriteLine("Тест на одинаковые идущие подряд биты провален.");
            if (PurposeTest(sequence, firstError))
                Console.WriteLine("Тест на самую длинную последовательность из единиц в блоке пройден.");
            else
                Console.WriteLine("Тест на самую длинную последовательность из единиц в блоке провален.");
        }
        public static bool MonobitsTest(bool[] sequence,double firstError)
        {
            int len = sequence.Length;
            double sobs = 0;
            foreach (var element in sequence)
                if (element)
                    sobs++;
                else
                    sobs--;
            sobs =Math.Abs(sobs)/Math.Sqrt(len);
            if (Additional.Erfc(sobs / Math.Sqrt(2)) > firstError)
                return true;
            return false;
        }

        public static bool RunsTest(bool[] sequence, double firstError)
        {
            int len = sequence.Length;
            int oneNum = 0;
            foreach (var element in sequence)
                if (element)
                    oneNum++;
            double pi = (double)oneNum / len;
            if (Math.Abs(pi - 0.5) >= 2 / Math.Sqrt(len))
                return false;
            int signSwitchNum = 0;
            for (int i = 1; i < len; i++)
                if (sequence[i - 1] != sequence[i])
                    signSwitchNum++;
            double pValue = Additional.Erfc(Math.Abs(signSwitchNum - 2 * len * pi * (1 - pi)) /
                                            (2 * Math.Sqrt(2 * len) * pi * (1 - pi)));
            if (pValue >= firstError)
                return true;
            return false;
        }

        public static bool PurposeTest(bool[] sequence, double firstError)
        {
            int len = sequence.Length;
            int[] oneNums = new int[len / 10000];
            for (int i = 0; i < len-10000; i += 10000)
            {
                int maxOneNum = 0;
                int curMaxOneNum = 0;
                for (int j = i; j < i+10000; j++)
                {
                    if (sequence[j])
                        curMaxOneNum++;
                    else if (curMaxOneNum > maxOneNum)
                    {
                        maxOneNum = curMaxOneNum;
                        curMaxOneNum = 0;
                    }
                }
                oneNums[i / 10000] = maxOneNum;
            }

            int[] statistic = new int[7];
            foreach (var element in oneNums)
            {
                if (element <= 10)
                    statistic[0]++;
                if (element == 11)
                    statistic[1]++;
                if (element == 12)
                    statistic[2]++;
                if (element == 13)
                    statistic[3]++;
                if (element == 14)
                    statistic[4]++;
                if (element == 15)
                    statistic[5]++;
                if (element >=16)
                    statistic[6]++;
            }

            double hi = 0;
            double[] pi = {0.0882, 0.2092, 0.2483, 0.1933, 0.1208,0.0675,0.0727};
            for (int i = 0; i < 7; i++)
                hi += Math.Pow(statistic[i] - 75 * pi[i], 2) / (75 * pi[i]);
            if (Additional.Igamc(3, hi / 2) > firstError)
                return true;
            return false;
        }
    }
}