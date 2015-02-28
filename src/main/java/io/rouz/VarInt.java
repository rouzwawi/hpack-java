package io.rouz;

public final class VarInt {

  private VarInt() {
  }

  public static int decode(int n, byte[] number) {
    return decode(n, number, 0);
  }

  public static int decode(int n, byte[] number, int p) {
    int limit = (1 << n) - 1;
    int x = number[p++] & limit;

    if (x < limit) {
      return x;
    } else {
      int m = 0, b;
      do {
        b = number[p++];
        x += (b & 127) * (1 << m);
        m += 7;
      } while ((b & 128) == 128);
      return x;
    }
  }

  public static int encode(int x, int n, byte[] number) {
    return encode(x, n, number, 0);
  }

  public static int encode(int x, int n, byte[] number, int p) {
    return encodeInternal(x, (1 << n) - 1, number, p);
  }

  private static int encodeInternal(int x, int limit, byte[] number, int p) {
    if (x < limit) {
      number[p++] |= (byte) (x & 0xFF);
    } else {
      number[p++] |= limit;
      x = x - limit;
      while (x >= 128) {
        number[p++] = (byte) (((x % 128) + 128) & 0xFF);
        x = x / 128;
      }
      number[p++] = (byte) (x & 0xFF);
    }

    return p;
  }

}
