using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace lab2
{
    static class Vigenere
    {
        static string alphabet = "abcdefghijklmnopqrstuvwxyz";
        public static string Cipher(string plaintext, string key)
        {
            string ciphertext = "";
            int keyLength = key.Length;
            for (int i = 0; i < plaintext.Length; i++)
            {
                ciphertext += (char)((plaintext[i] + key[i % keyLength] - 2 * alphabet[0]) % alphabet.Length + alphabet[0]);
            }
            return ciphertext;
        }

        public static string Decipher(string ciphertext, string key)
        {
            string plaintext = "";
            int keyLength = key.Length;
            for (int i = 0; i < ciphertext.Length; i++)
            {
                plaintext += (char)((ciphertext[i] - key[i % keyLength] + alphabet.Length) % alphabet.Length + alphabet[0]);
            }
            return plaintext;
        }

        public static string IndexOfCoincidence(string ciphertext)
        {
            int[] letterFrequencies = new int[alphabet.Length];
            int keyLength = 3;
            int length;
            double index;
            double prevIndex = 0.03;
            bool b = false;
            for (int j = 2; j < ciphertext.Length / 10; j++)
            {
                for (int i = 0; i < alphabet.Length; i++)
                {
                    letterFrequencies[i] = 0;
                }
                index = 0;
                length = (ciphertext.Length - 1) / j + 1;
                for (int i = 0; i < ciphertext.Length; i += j)
                {
                    letterFrequencies[ciphertext[i] - alphabet[0]]++;
                }
                for (int i = 0; i < alphabet.Length; i++)
                {
                    index += (double)letterFrequencies[i] * (letterFrequencies[i] - 1) / length / (length - 1);
                }
                if (Math.Abs(index - prevIndex) > 0.01)
                {
                    if (!b)
                    {
                        keyLength = j;
                        b = true;
                    }
                    else break;
                }
                else
                {
                    b = false;
                }
                prevIndex = index;
            }

            int s = 0;
            double maxIndex;
            length = (ciphertext.Length - 1) / keyLength + 1;
            int[] relShift = new int[keyLength];
            relShift[0] = 0;
            int[] letterFrequencies2 = new int[alphabet.Length];
            for (int i = 0; i < alphabet.Length; i++)
            {
                letterFrequencies[i] = 0;
            }
            for (int i = 0; i < ciphertext.Length; i += keyLength)
            {
                letterFrequencies[ciphertext[i] - alphabet[0]]++;
            }
            for (int j = 1; j < keyLength; j++)
            {
                maxIndex = 0;
                for (int k = 1; k < alphabet.Length; k++)
                {
                    index = 0;
                    for (int m = 0; m < alphabet.Length; m++)
                    {
                        letterFrequencies2[m] = 0;
                    }
                    for (int m = j; m < ciphertext.Length; m += keyLength)
                    {
                        letterFrequencies2[(ciphertext[m] - alphabet[0] + k) % alphabet.Length]++;
                    }
                    for (int m = 0; m < alphabet.Length; m++)
                    {
                        index += (double)letterFrequencies[m] * letterFrequencies2[m] / length / length;
                    }

                    if (index > maxIndex)
                    {
                        maxIndex = index;
                        s = k;
                    }
                }
                relShift[j] = s;
                //relShift[j] = alphabet.Length - s;
            }

            int[] res = new int[ciphertext.Length];
            //string res = "";

            for (int i = 0; i < ciphertext.Length; i++)
            {
                res[i] = (ciphertext[i] - alphabet[0] + relShift[i % keyLength]) % alphabet.Length;
                //res += (char)((ciphertext[i] - alphabet[0] - relShift[i % keyLength] + alphabet.Length) % alphabet.Length + alphabet[0]);
            }
            for (int i = 0; i < alphabet.Length; i++)
            {
                letterFrequencies[i] = 0;
            }
            for (int i = 0; i < ciphertext.Length; i++)
            {
                letterFrequencies[res[i]]++;
            }
            int key = 0;
            int maxFr = 0;
            for (int i = 0; i < letterFrequencies.Length; i++)
            {
                if (letterFrequencies[i] > maxFr)
                {
                    key = 4 - i;
                    maxFr = letterFrequencies[i];
                }
            }
            key = (alphabet.Length + key) % alphabet.Length;
            string finalResult = "";
            for (int i = 0; i < ciphertext.Length; i++)
            {
                finalResult += (char)((res[i] + key) % alphabet.Length + alphabet[0]);
            }
            return finalResult;
        }

        public static bool KeyIsValid(string key)
        {
            foreach (var c in key)
            {
                if (!alphabet.Contains(c))
                {
                    return false;
                }
            }
            return true;
        }
    }

    static class SimpleSubstitution
    {
        static string alphabet = "abcdefghijklmnopqrstuvwxyz";
        static string frequenciesAlphabet = "etaoinshrdlcumwfgypbvkxjqz";
        static string bigramFrequencies = "thheineranrendatonnthaesstenedtoit";

        public static string Cipher(string plaintext, string key)
        {
            string ciphertext = "";
            foreach (var c in plaintext)
            {
                ciphertext += key[c - alphabet[0]];
            }
            return ciphertext;
        }

        public static string Decipher(string ciphertext, string key)
        {
            string plaintext = "";
            foreach (var c in ciphertext)
            {
                plaintext += (char)(alphabet[0] + key.IndexOf(c));
            }
            return plaintext;
        }

        public static string FrequencyAnalysis(string ciphertext)
        {
            char[] reverseKey = new char[alphabet.Length];
            (int, int)[] letterFrequencies = new (int, int)[alphabet.Length];
            for (int i = 0; i < alphabet.Length; i++)
            {
                letterFrequencies[i] = (i, 0);
            }
            foreach (var c in ciphertext)
            {
                letterFrequencies[c - alphabet[0]].Item2++;
            }
            Array.Sort(letterFrequencies, (a, b) => b.Item2.CompareTo(a.Item2));
            for (int i = 0; i < alphabet.Length; i++)
            {
                reverseKey[letterFrequencies[i].Item1] = frequenciesAlphabet[i];
            }
            return Cipher(ciphertext, new string(reverseKey));
        }

        public static bool KeyIsValid(string key)
        {
            return key.Length == alphabet.Length && !key.Except(alphabet).Any();
        }
    }
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length != 4)
            {
                Console.WriteLine("Invalid number of arguments");
                return;
            }
            string keyFilename = args[0], textFilename = args[1], actionChoice = args[2], cipherChoice = args[3];
            string key, text, result = "";

            using (StreamReader sr = new StreamReader(keyFilename, System.Text.Encoding.Default))
            {
                key = sr.ReadLine();
            }
            using (StreamReader sr = new StreamReader(textFilename, System.Text.Encoding.Default))
            {
                text = sr.ReadLine();
            }
            switch (cipherChoice)
            {
                case "s":
                    {
                        if (!SimpleSubstitution.KeyIsValid(key))
                        {
                            Console.WriteLine("Key is not valid");
                            return;
                        }
                        switch (actionChoice)
                        {
                            case "c":
                                result = SimpleSubstitution.Cipher(text, key);
                                break;
                            case "d":
                                result = SimpleSubstitution.Decipher(text, key);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case "v":
                    {
                        if (!Vigenere.KeyIsValid(key))
                        {
                            Console.WriteLine("Key is not valid");
                            return;
                        }
                        switch (actionChoice)
                        {
                            case "c":
                                result = Vigenere.Cipher(text, key);
                                break;
                            case "d":
                                result = Vigenere.Decipher(text, key);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
            Console.WriteLine("Result:");
            Console.WriteLine(result);

            Console.WriteLine("\nFrequency analysis example:");
            Console.WriteLine("Enter name of the file with ciphertext:");
            textFilename = Console.ReadLine();
            using (StreamReader sr = new StreamReader(textFilename, System.Text.Encoding.Default))
            {
                text = sr.ReadLine();
            }
            Console.WriteLine(SimpleSubstitution.FrequencyAnalysis(text));

            Console.WriteLine("\nIndex of coincidence atack example:");
            Console.WriteLine("Enter name of the file with ciphertext:");
            textFilename = Console.ReadLine();
            using (StreamReader sr = new StreamReader(textFilename, System.Text.Encoding.Default))
            {
                text = sr.ReadLine();
            }
            Console.WriteLine(Vigenere.IndexOfCoincidence(text));
        }
    }
}
