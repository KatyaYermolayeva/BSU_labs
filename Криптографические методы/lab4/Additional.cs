using System;
using Extreme.Mathematics.Calculus;
namespace Lab_4
{
    public class Additional
    {
        public static bool[] GenerateSequence(double weight, int len)
        {
            bool[] sequence = new bool[len];
            Random rnd = new Random();
            for (int i = 0; i < len; i++)
                sequence[i] = rnd.NextDouble() < weight;
            return sequence;
        }

        public static void WriteSequence(bool[] sequence)
        {
            foreach (var element in sequence)
                if(element)
                    Console.Write("1");
                else
                    Console.Write("0");
            Console.WriteLine();
        }
        public static double Erfc(double value)
        {
            AdaptiveIntegrator integrator = new AdaptiveIntegrator();
            Func<double, double> f1 = (x) =>Math.Exp(-Math.Pow(x,2));
            integrator.Integrate(f1, 0, value);
            return 1 - integrator.Result *2/Math.Sqrt(Math.PI);
        }
        public static double Igamc(double firstParametr, double secondParametr)
        {
            AdaptiveIntegrator integrator = new AdaptiveIntegrator();
            Func<double, double> f1 = (x) =>Math.Pow(x,firstParametr-1)*Math.Exp(-x);
            integrator.Integrate(f1, 0, secondParametr);
            return Gamma(firstParametr)-integrator.Result;
        }
        public static double Gamma(double value)
        {
            AdaptiveIntegrator integrator = new AdaptiveIntegrator();
            Func<double, double> f1 = (x) =>Math.Pow(-Math.Log(x),value-1);
            integrator.Integrate(f1, 0, 1);
            return integrator.Result;
        }

        public static bool[] ConvertByteToBool(byte[] bytes)
        {
            int len = bytes.Length;
            bool[] result = new bool[8*len];
            for (int i=0;i<len;i++)
            {
                for (int j = 0; j<8; j++)
                    result[i*8+j] = (bytes[i] & (1 << j)) != 0;
                Array.Reverse(result,i*8,8); 
            }
            return result;
        }
        public static byte[] ConvertBoolToByte(bool[] source)
        {
            int len = source.Length / 8;
            byte[] bytes = new byte[len];
            for (int i = 0; i < len; i++)
            {
                bytes[i] = 0;
                int index = 0;
                for (int j=0;j<8;j++)
                {
                    if (source[8*i+j])
                        bytes[i]|= (byte)(1 << (7 - index));
                    index++;
                }
            }
            return bytes;
        }

        public static void TestWithWeight(double weightOpenText,double weightKey, double firstError)
        {
            bool[] key = new bool[128];
            byte[] keyByte = new byte[16];
            bool[] openText = new bool[8388608];
            byte[] openTextByte = new byte[1048576];
            bool[] cipherText = new bool[8388608];
            byte[] cipherTextByte = new byte[1048576];
            byte[] openTextByteBlock = new byte[16];
            byte[] cipherTextByteBlock = new byte[16];
            AES cipher = new AES(4);
            key = GenerateSequence(weightKey,128);
            keyByte = ConvertBoolToByte(key);
            openText = GenerateSequence(weightOpenText,8388608);
            openTextByte = ConvertBoolToByte(openText);
            int len = openTextByte.Length;
            for (int i = 0; i < len; i += 16)
            {
                for (int j = 0; j < 16; j++)
                {
                    openTextByteBlock[j] = openTextByte[i + j];
                }
                cipherTextByteBlock = cipher.Encrypt(keyByte, openTextByteBlock);
                for (int j = 0; j < 16; j++)
                {
                    cipherTextByte[i+j] = cipherTextByteBlock[j];
                }
            }
            cipherText =ConvertByteToBool(cipherTextByte);
            NistTests.NistBattery(cipherText,firstError);
        }
        public static void TestWithChain(double firstError)
        {
            bool[] key = new bool[128];
            byte[] keyByte = new byte[16];
            bool[] cipherText = new bool[8388608];
            byte[] cipherTextByte = new byte[1048576];
            byte[] cipherTextByteBlock = new byte[16];
            AES cipher = new AES(4);
            key = GenerateSequence(0.5,128);
            keyByte = ConvertBoolToByte(key);
            int len = cipherTextByte.Length;
            for (int i = 0; i < len; i += 16)
            {
                for (int j = 0; j < 16; j++)
                {
                    cipherTextByte[i+j] = cipherTextByteBlock[j];
                }
                cipherTextByteBlock = cipher.Encrypt(keyByte, cipherTextByteBlock);
            }
            cipherText =ConvertByteToBool(cipherTextByte);
            NistTests.NistBattery(cipherText,firstError);
        }
        public static void TestWithCorrelation(double firstError)
        {
            bool[] key = new bool[128];
            byte[] keyByte = new byte[16];
            bool[] openText = new bool[8388608];
            byte[] openTextByte = new byte[1048576];
            bool[] cipherText = new bool[8388608];
            byte[] cipherTextByte = new byte[1048576];
            byte[] openTextByteBlock = new byte[16];
            byte[] cipherTextByteBlock = new byte[16];
            byte[] outputTextByteBlock = new byte[16];
            bool[] openTextBoolBlock = new bool[128];
            bool[] cipherTextBoolBlock = new bool[128];
            bool[] outputTextBoolBlock = new bool[128];
            AES cipher = new AES(4);
            key = GenerateSequence(0.5,128);
            keyByte = ConvertBoolToByte(key);
            openText = GenerateSequence(0.5,8388608);
            openTextByte = ConvertBoolToByte(openText);
            int len = openTextByte.Length;
            for (int i = 0; i < len; i += 16)
            {
                for (int j = 0; j < 16; j++)
                {
                    openTextByteBlock[j] = openTextByte[i + j];
                }
                cipherTextByteBlock = cipher.Encrypt(keyByte, openTextByteBlock);
                cipherTextBoolBlock = ConvertByteToBool(cipherTextByteBlock);
                openTextBoolBlock = ConvertByteToBool(openTextByteBlock);
                for (int j = 0; j < 128; j++)
                {
                    if (cipherTextBoolBlock[j] != openTextBoolBlock[j])
                        outputTextBoolBlock[j] = true;
                    else
                        outputTextBoolBlock[j] = false;
                }

                outputTextByteBlock = ConvertBoolToByte(outputTextBoolBlock);
                for (int j = 0; j < 16; j++)
                {
                    cipherTextByte[i+j] = outputTextByteBlock[j];
                }
            }
            cipherText =ConvertByteToBool(cipherTextByte);
            NistTests.NistBattery(cipherText,firstError);
        }
        public static void TestWithKeyError(double firstError)
        {
            bool[] key;
            byte[] keyByte = new byte[16];
            bool[] errorKey = new bool[128];
            byte[] errorKeyByte = new byte[16];
            bool[] openText = new bool[8388608];
            byte[] openTextByte = new byte[1048576];
            bool[] cipherText = new bool[8388608];
            byte[] cipherTextByte = new byte[1048576];
            byte[] openTextByteBlock = new byte[16];
            byte[] cipherTextByteBlock = new byte[16];
            byte[] errorCipherTextByteBlock = new byte[16];
            byte[] outputTextByteBlock = new byte[16];
            bool[] cipherTextBoolBlock = new bool[128];
            bool[] errorCipherTextBoolBlock = new bool[128];
            bool[] outputTextBoolBlock = new bool[128];
            AES cipher = new AES(4);
            key = GenerateSequence(0.5,128);
            keyByte = ConvertBoolToByte(key);
            openText = GenerateSequence(0.5,8388608);
            openTextByte = ConvertBoolToByte(openText);
            int len = openTextByte.Length;
            for (int i = 0; i < len; i += 16)
            {
                for (int j = 0; j < 16; j++)
                {
                    openTextByteBlock[j] = openTextByte[i + j];
                }
                cipherTextByteBlock = cipher.Encrypt(keyByte, openTextByteBlock);
                errorKey = (bool[])key.Clone();
                errorKey[i * 8 % 128] = !errorKey[i * 8 % 128];
                errorKeyByte = ConvertBoolToByte(errorKey);
                errorCipherTextByteBlock = cipher.Encrypt(errorKeyByte, openTextByteBlock);
                errorCipherTextBoolBlock = ConvertByteToBool(errorCipherTextByteBlock);
                cipherTextBoolBlock = ConvertByteToBool(cipherTextByteBlock);
                for (int j = 0; j < 128; j++)
                {
                    if (cipherTextBoolBlock[j] != errorCipherTextBoolBlock[j])
                        outputTextBoolBlock[j] = true;
                    else
                        outputTextBoolBlock[j] = false;
                }

                outputTextByteBlock = ConvertBoolToByte(outputTextBoolBlock);
                for (int j = 0; j < 16; j++)
                {
                    cipherTextByte[i+j] = outputTextByteBlock[j];
                }
            }
            cipherText =ConvertByteToBool(cipherTextByte);
            NistTests.NistBattery(cipherText,firstError);
        }
        public static void TestWithOpenTextError(double firstError)
        {
            bool[] key;
            byte[] keyByte;
            bool[] errorOpenTextBoolBlock;
            byte[] errorOpenTextByteBlock;
            bool[] openText;
            byte[] openTextByte;
            bool[] cipherText;
            byte[] cipherTextByte = new byte[1048576];
            byte[] openTextByteBlock = new byte[16];
            byte[] cipherTextByteBlock;
            byte[] errorCipherTextByteBlock;
            byte[] outputTextByteBlock;
            bool[] cipherTextBoolBlock;
            bool[] openTextBoolBlock;
            bool[] errorCipherTextBoolBlock;
            bool[] outputTextBoolBlock = new bool[128];
            AES cipher = new AES(4);
            key = GenerateSequence(0.5,128);
            keyByte = ConvertBoolToByte(key);
            openText = GenerateSequence(0.5,8388608);
            openTextByte = ConvertBoolToByte(openText);
            int len = openTextByte.Length;
            for (int i = 0; i < len; i += 16)
            {
                for (int j = 0; j < 16; j++)
                {
                    openTextByteBlock[j] = openTextByte[i + j];
                }

                openTextBoolBlock = ConvertByteToBool(openTextByteBlock);
                cipherTextByteBlock = cipher.Encrypt(keyByte, openTextByteBlock);
                errorOpenTextBoolBlock = (bool[])openTextBoolBlock.Clone();
                errorOpenTextBoolBlock[i * 8 % 128] = !errorOpenTextBoolBlock[i * 8 % 128];
                errorOpenTextByteBlock = ConvertBoolToByte(errorOpenTextBoolBlock);
                errorCipherTextByteBlock = cipher.Encrypt(keyByte, errorOpenTextByteBlock);
                errorCipherTextBoolBlock = ConvertByteToBool(errorCipherTextByteBlock);
                cipherTextBoolBlock = ConvertByteToBool(cipherTextByteBlock);
                for (int j = 0; j < 128; j++)
                {
                    if (cipherTextBoolBlock[j] != errorCipherTextBoolBlock[j])
                        outputTextBoolBlock[j] = true;
                    else
                        outputTextBoolBlock[j] = false;
                }

                outputTextByteBlock = ConvertBoolToByte(outputTextBoolBlock);
                for (int j = 0; j < 16; j++)
                {
                    cipherTextByte[i+j] = outputTextByteBlock[j];
                }
            }
            cipherText =ConvertByteToBool(cipherTextByte);
            NistTests.NistBattery(cipherText,firstError);
        }
    }
}