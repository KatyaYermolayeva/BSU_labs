using System;
using System.Collections.Generic;

namespace Lab_4
{
    class AES
    {
        int Nb = 4;
        int Nk;
        int Nr;
        byte[][] SBox;
        byte[][] InvSBox;
        byte[][] Rcon;

        public AES(int k)
        {
            switch (k)
            {
                case 4:
                    Nr = 10;
                    break;
                case 6:
                    Nr = 12;
                    break;
                case 8:
                    Nr = 14;
                    break;
                default:
                    throw new ArgumentException("Invalid key length");
            }

            Nk = k;

            SBox = new byte[16][];
            InvSBox = new byte[16][];
            List<byte> bytes = new List<byte>(256);
            Random random = new Random();
            for (int i = 0; i < bytes.Capacity; i++)
            {
                bytes.Add((byte) i);
            }

            for (int i = 0; i < SBox.Length; i++)
            {
                SBox[i] = new byte[16];
                InvSBox[i] = new byte[16];
                for (int j = 0; j < SBox[i].Length; j++)
                {
                    int index = random.Next(0, bytes.Count - 1);
                    SBox[i][j] = bytes[index];
                    bytes.RemoveAt(index);
                }
            }

            for (int i = 0; i < InvSBox.Length; i++)
            {
                for (int j = 0; j < InvSBox[i].Length; j++)
                {
                    InvSBox[SBox[i][j] / 16][SBox[i][j] % 16] = (byte) (i * 16 + j);
                }
            }

            Rcon = new byte[Nr + 1][];
            byte x = 2;
            Rcon[0] = new byte[4] {1, 0, 0, 0};
            Rcon[1] = new byte[4] {1, 0, 0, 0};
            for (int i = 2; i < Rcon.Length; i++)
            {
                Rcon[i] = new byte[4] {x, 0, 0, 0};
                x = Multiply(x, 2);
            }
        }

        byte Multiply(byte x, byte y, byte m = 27)
        {
            byte[] result = new byte[2];
            result[0] = 0;
            result[1] = 0;
            for (int i = 0; i < 8; i++)
            {
                for (int j = 0; j < 8; j++)
                {
                    int k = 1 - (i + j) / 8;
                    byte b = (byte) (((x >> i) & (y >> j) & 1) << ((i + j) % 8));
                    result[k] = (byte) (result[k] ^ b);
                }
            }

            for (int i = 7; i >= 0; i--)
            {
                if ((result[0] & (1 << i)) != 0)
                {
                    result[0] = (byte) (result[0] ^ (m >> (8 - i)));
                    result[1] = (byte) (result[1] ^ (m << i));
                }
            }

            return result[1];
        }

        public void SetSBox(byte[][] s)
        {
            if (s.Length != 16 || s[0].Length != 16)
            {
                throw new ArgumentException("Invalid SBox size");
            }

            SBox = s;
        }

        byte SubByte(byte b)
        {
            return SBox[b / 16][b % 16];
        }

        byte InvSubByte(byte b)
        {
            return InvSBox[b / 16][b % 16];
        }

        byte[] ShiftRow(byte[] b, int n)
        {
            byte[] result = new byte[b.Length];
            for (int i = 0; i < n; i++)
            {
                result[b.Length - n + i] = b[i];
            }

            for (int i = n; i < b.Length; i++)
            {
                result[i - n] = b[i];
            }

            return result;
        }

        byte[][] MixColumns(byte[][] s)
        {
            byte[][] result = new byte[s.Length][];
            for (int i = 0; i < result.Length; i++)
            {
                result[i] = new byte[s[i].Length];
            }

            byte[] buf = new byte[4];
            for (int j = 0; j < s[0].Length; j++)
            {
                buf[0] = (byte) (Multiply(2, s[0][j]) ^ Multiply(3, s[1][j]) ^ s[2][j] ^ s[3][j]);
                buf[1] = (byte) (Multiply(2, s[1][j]) ^ Multiply(3, s[2][j]) ^ s[0][j] ^ s[3][j]);
                buf[2] = (byte) (Multiply(2, s[2][j]) ^ Multiply(3, s[3][j]) ^ s[0][j] ^ s[1][j]);
                buf[3] = (byte) (Multiply(2, s[3][j]) ^ Multiply(3, s[0][j]) ^ s[1][j] ^ s[2][j]);
                result[0][j] = buf[0];
                result[1][j] = buf[1];
                result[2][j] = buf[2];
                result[3][j] = buf[3];
            }

            return result;
        }

        byte[][] InvMixColumns(byte[][] s)
        {
            byte[][] result = new byte[s.Length][];
            for (int i = 0; i < result.Length; i++)
            {
                result[i] = new byte[s[i].Length];
            }

            byte[] buf = new byte[4];
            for (int j = 0; j < s[0].Length; j++)
            {
                buf[0] = (byte) (Multiply(14, s[0][j]) ^ Multiply(11, s[1][j]) ^ Multiply(13, s[2][j]) ^
                                 Multiply(9, s[3][j]));
                buf[1] = (byte) (Multiply(9, s[0][j]) ^ Multiply(14, s[1][j]) ^ Multiply(11, s[2][j]) ^
                                 Multiply(13, s[3][j]));
                buf[2] = (byte) (Multiply(13, s[0][j]) ^ Multiply(9, s[1][j]) ^ Multiply(14, s[2][j]) ^
                                 Multiply(11, s[3][j]));
                buf[3] = (byte) (Multiply(11, s[0][j]) ^ Multiply(13, s[1][j]) ^ Multiply(9, s[2][j]) ^
                                 Multiply(14, s[3][j]));
                result[0][j] = buf[0];
                result[1][j] = buf[1];
                result[2][j] = buf[2];
                result[3][j] = buf[3];
            }

            return result;
        }

        byte[][] AddRoundKey(byte[][] s, byte[][] key, int round)
        {
            for (int i = 0; i < Nb; i++)
            {
                for (int j = 0; j < 4; j++)
                {
                    s[j][i] = (byte) (s[j][i] ^ key[round * Nb + i][j]);
                }
            }

            return s;
        }

        byte[][] KeyExpansion(byte[] key)
        {
            byte[][] roundKeys = new byte[Nb * (Nr + 1)][];
            byte[] temp = new byte[4];
            for (int i = 0; i < Nk; i++)
            {
                roundKeys[i] = new byte[4];
                for (int j = 0; j < 4; j++)
                {
                    roundKeys[i][j] = key[i * 4 + j];
                }
            }

            for (int i = Nk; i < roundKeys.Length; i++)
            {
                roundKeys[i] = new byte[4];
                temp = roundKeys[i - 1];
                if (i % Nk == 0)
                {
                    temp = ShiftRow(temp, 1);
                    for (int j = 0; j < 4; j++)
                    {
                        temp[j] = SubByte(temp[j]);
                        temp[j] ^= Rcon[i / Nk][j];
                    }
                }
                else if (Nk > 6 && i % Nk == 4)
                {
                    for (int j = 0; j < 4; j++)
                    {
                        temp[j] = SubByte(temp[j]);
                    }
                }

                for (int j = 0; j < 4; j++)
                {
                    roundKeys[i][j] = (byte) (roundKeys[i - Nk][j] ^ temp[j]);
                }
            }

            return roundKeys;
        }

        public byte[] Encrypt(byte[] key, byte[] block)
        {
            if (block.Length != Nb * 4)
            {
                throw new ArgumentException("Invalid block size");
            }

            if (key.Length != Nk * 4)
            {
                throw new ArgumentException("Invalid key size");
            }

            byte[][] s = new byte[4][];
            for (int i = 0; i < 4; i++)
            {
                s[i] = new byte[Nb];
            }

            for (int i = 0; i < block.Length; i++)
            {
                s[i / Nb][i % Nb] = block[i];
            }

            byte[][] roundKeys = KeyExpansion(key);

            s = AddRoundKey(s, roundKeys, 0);
            for (int k = 0; k < Nr - 1; k++)
            {
                for (int i = 0; i < s.Length; i++)
                {
                    for (int j = 0; j < s[i].Length; j++)
                    {
                        s[i][j] = SubByte(s[i][j]);
                    }

                    s[i] = ShiftRow(s[i], i);
                }

                s = MixColumns(s);
                s = AddRoundKey(s, roundKeys, k + 1);
            }

            for (int i = 0; i < s.Length; i++)
            {
                for (int j = 0; j < s[i].Length; j++)
                {
                    s[i][j] = SubByte(s[i][j]);
                }

                s[i] = ShiftRow(s[i], i);
            }

            s = AddRoundKey(s, roundKeys, Nr);

            byte[] result = new byte[block.Length];
            for (int i = 0; i < block.Length; i++)
            {
                result[i] = s[i / Nb][i % Nb];
            }

            return result;
        }

        public byte[] Decrypt(byte[] block, byte[] key)
        {
            if (block.Length != Nb * 4)
            {
                throw new ArgumentException("Invalid block size");
            }

            if (key.Length != Nk * 4)
            {
                throw new ArgumentException("Invalid key size");
            }

            byte[][] s = new byte[4][];
            for (int i = 0; i < 4; i++)
            {
                s[i] = new byte[Nb];
            }

            for (int i = 0; i < block.Length; i++)
            {
                s[i / Nb][i % Nb] = block[i];
            }

            byte[][] roundKeys = KeyExpansion(key);

            s = AddRoundKey(s, roundKeys, Nr);
            for (int k = Nr - 2; k >= 0; k--)
            {
                for (int i = 0; i < s.Length; i++)
                {
                    s[i] = ShiftRow(s[i], (s[i].Length - i) % s[i].Length);
                    for (int j = 0; j < s[i].Length; j++)
                    {
                        s[i][j] = InvSubByte(s[i][j]);
                    }
                }

                s = AddRoundKey(s, roundKeys, k + 1);
                s = InvMixColumns(s);
            }

            for (int i = 0; i < s.Length; i++)
            {
                s[i] = ShiftRow(s[i], (s[i].Length - i) % s[i].Length);
                for (int j = 0; j < s[i].Length; j++)
                {
                    s[i][j] = InvSubByte(s[i][j]);
                }
            }

            s = AddRoundKey(s, roundKeys, 0);
            byte[] result = new byte[block.Length];
            for (int i = 0; i < block.Length; i++)
            {
                result[i] = s[i / Nb][i % Nb];
            }

            return result;
        }
    }
}
