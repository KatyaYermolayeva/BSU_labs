using System;

namespace Lab_4
{
    internal class Program
    {
        public static void Main()
        {
            const double firstError = 0.05;

            Console.WriteLine("Результаты тестов при случайном ключе и случайном открытом тексте: ");
            Additional.TestWithWeight(0.5,0.5,firstError);
            Console.WriteLine();
            
            Console.WriteLine("Результаты тестов при случайном ключе и открытом тексте с малым весом: ");
            Additional.TestWithWeight(0.05,0.5,firstError);
            Console.WriteLine();

            Console.WriteLine("Результаты тестов при случайном ключе и открытом тексте с большим весом: ");
            Additional.TestWithWeight(0.95,0.5,firstError);
            Console.WriteLine();

            Console.WriteLine("Результаты тестов при ключе с малым весом и случайном открытом тексте: ");
            Additional.TestWithWeight(0.5,0.05,firstError);
            Console.WriteLine();
            
            Console.WriteLine("Результаты тестов при ключе с большим весом и случайном открытом тексте: ");
            Additional.TestWithWeight(0.5,0.95,firstError);
            Console.WriteLine();
            
            Console.WriteLine("Результаты тестов при цепочной обработке: ");
            Additional.TestWithChain(firstError);
            Console.WriteLine();
            
            Console.WriteLine("Результаты тестов с размножением ошибки в открытом тексте: ");
            Additional.TestWithOpenTextError(firstError);
            Console.WriteLine();
            
            Console.WriteLine("Результаты тестов с размножением ошибки в ключе: ");
            Additional.TestWithKeyError(firstError);
            Console.WriteLine();
            
            Console.WriteLine("Результаты тестов при исследовании корреляции открытого текста и шифр-текста: ");
            Additional.TestWithCorrelation(firstError);
            Console.WriteLine();

        }
    }
}