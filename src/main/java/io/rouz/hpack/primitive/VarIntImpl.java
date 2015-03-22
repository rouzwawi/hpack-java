package io.rouz.hpack.primitive;

import java.nio.ByteBuffer;

final class VarIntImpl implements VarInt {

  VarIntImpl() {
  }

  @Override
  public int decode(int n, ByteBuffer buffer) {
    int limit = (1 << n) - 1;
    int x = buffer.get() & limit;

    if (x < limit) {
      return x;
    } else {
      int m = 0, b;
      do {
        b = buffer.get();
        x += (b & 127) * (1 << m);
        m += 7;
      } while ((b & 128) == 128);
      return x;
    }
  }

  @Override
  public int encode(int x, int n, ByteBuffer buffer) {
    if (x < 0) {
      throw new IllegalArgumentException("Can't encode negative number: " + x);
    }

    if (n <= 0 || n > 8) {
      throw new IllegalArgumentException("Prefix must be in range [1,8], was: " + n);
    }

    return encodeInternal(x, (1 << n) - 1, buffer);
  }

  private static int encodeInternal(int x, int limit, ByteBuffer buffer) {
    final int initial = buffer.position();

    if (x < limit) {
      buffer.put((byte) (buffer.get(buffer.position()) | (x & 0xFF)));
    } else {
      buffer.put((byte) (buffer.get(buffer.position()) | limit));
      x = x - limit;
      while (x >= 128) {
        buffer.put((byte) (((x % 128) + 128) & 0xFF));
        x = x / 128;
      }
      buffer.put((byte) (x & 0xFF));
    }

    return buffer.position() - initial;
  }

}
