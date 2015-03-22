package io.rouz.hpack;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Random;

import io.rouz.hpack.primitive.VarInt;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class VarIntTest {

  VarInt varInt = VarInt.INSTANCE;

  @Test
  public void shouldDoBasicEncodingDecoding() {
    ByteBuffer buffer = ByteBuffer.allocate(11);

    varInt.encode(1337, 5, buffer);
    varInt.encode(10, 5, buffer);
    varInt.encode(42, 8, buffer);
    varInt.encode(Integer.MAX_VALUE, 6, buffer);

    byte[] expected = new byte[] {
        (byte) 0x1f, (byte) 0x9a, (byte) 0x0a, // 1337 prefix 5
        (byte) 0x0a, // 10 prefix 5
        (byte) 0x2a, // 42 prefix 8
        (byte) 0x3f, (byte) 0xc0,(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x07
    };

    for (int i = 0; i < expected.length; i++) {
      assertThat("byte " + i, buffer.get(i), is(expected[i]));
    }

    buffer.rewind();

    assertThat(varInt.decode(5, buffer), is(1337));
    assertThat(varInt.decode(5, buffer), is(10));
    assertThat(varInt.decode(8, buffer), is(42));
    assertThat(varInt.decode(6, buffer), is(Integer.MAX_VALUE));
  }

  @Test
  public void shouldReturnNumberOfBytesWritten() {
    ByteBuffer buffer = ByteBuffer.allocate(8);

    int b1 = varInt.encode(1337, 5, buffer);
    assertThat(b1, is(3));
    assertThat(buffer.position(), is(3));
  }

  @Test
  public void shouldReturnNumberOfBytesConsumed() {
    ByteBuffer buffer = ByteBuffer.allocate(8);

    varInt.encode(1337, 5, buffer);

    buffer.rewind();
    int x = varInt.decode(5, buffer);

    assertThat(x, is(1337));
    assertThat(buffer.position(), is(3));
  }

  @Test
  public void shouldWorkOnArbitraryIntegers() {
    Random rnd = new Random();
    ByteBuffer buffer = ByteBuffer.allocate(8);

    for (int i = 0; i < 10000; i++) {
      buffer.rewind();
      for (int j = 0; j < buffer.limit(); j++) {
        buffer.put(j, (byte) 0);
      }

      int x = rnd.nextInt(Integer.MAX_VALUE);
      int n = rnd.nextInt(8) + 1;

      varInt.encode(x, n, buffer);
      buffer.rewind();
      int xx = varInt.decode(n, buffer);

      assertThat(xx, is(x));
      assertThat(buffer.get(0) & (~((1 << n) - 1)) & 0xFF, is(0));
    }
  }

  @Test
  public void shouldNotChangeMSB() {
    int prefix = 5;
    int msbs = 8 - prefix;

    for (int i = 0; i < (1 << msbs); i++) {
      ByteBuffer buffer = ByteBuffer.allocate(1);
      buffer.put(0, (byte) i);

      varInt.encode(3, prefix, buffer);
      assertThat(buffer.get(0), is((byte) (0x03 | i)));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowOnNegativeNumber() {
    varInt.encode(-1337, 5, ByteBuffer.allocate(8));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowOnNegativePrefix() {
    varInt.encode(1337, -1, ByteBuffer.allocate(8));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowOnZeroPrefix() {
    varInt.encode(1337, 0, ByteBuffer.allocate(8));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowOnLargePrefix() {
    varInt.encode(1337, 9, ByteBuffer.allocate(8));
  }
}
