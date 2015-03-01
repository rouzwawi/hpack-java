package io.rouz;

import org.junit.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class VarIntTest {

  VarInt varInt = VarInt.INSTANCE;

  @Test
  public void shouldDoBasicEncodingDecoding() throws Exception {
    byte[] number = new byte[11];
    int b1, b2, b3, b4;

    b1 = varInt.encode(1337, 5, number, 0);
    b2 = varInt.encode(10, 5, number, b1);
    b3 = varInt.encode(42, 8, number, b2);
    b4 = varInt.encode(Integer.MAX_VALUE, 6, number, b3);

    assertThat(b4, is(11));
    printBinary(number, b4);

    byte[] expected = new byte[] {
        (byte) 0x1f, (byte) 0x9a, (byte) 0x0a, // 1337 prefix 5
        (byte) 0x0a, // 10 prefix 5
        (byte) 0x2a, // 42 prefix 8
        (byte) 0x3f, (byte) 0xc0,(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x07
    };

    for (int i = 0; i < expected.length; i++) {
      assertThat("byte " + i, number[i], is(expected[i]));
    }

    assertThat(varInt.decode(5, number, 0), is(1337));
    assertThat(varInt.decode(5, number, b1), is(10));
    assertThat(varInt.decode(8, number, b2), is(42));
    assertThat(varInt.decode(6, number, b3), is(Integer.MAX_VALUE));
  }

  @Test
  public void shouldWorkOnArbitraryIntegers() throws Exception {
    Random rnd = new Random();
    byte[] number = new byte[8];

    for (int i = 0; i < 10000; i++) {
      for (int j = 0; j < number.length; j++) {
        number[j] = 0;
      }

      int x = rnd.nextInt(Integer.MAX_VALUE);
      int n = rnd.nextInt(8) + 1;

      varInt.encode(x, n, number);
      int xx = varInt.decode(n, number);

      assertThat(xx, is(x));
      assertThat(number[0] & (~((1 << n) - 1)) & 0xFF, is(0));
    }
  }

  private static void printBinary(byte[] number, int bytes) {
    for (int i = 0; i < bytes; i++) {
      printBinary(number[i]);
    }
  }

  private static void printBinary(final byte b) {
    int mask = 0x80;
    for (int i = 0; i < 8; i++) {
      System.out.print((b & mask) != 0 ? '1' : '0');
      mask >>= 1;
    }
    System.out.println(" | " + String.format("%02X", b).toLowerCase());
  }
}
