package io.rouz.hpack;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.nio.ByteBuffer;
import java.util.Random;

import io.rouz.hpack.primitive.HuffmanCodec;
import io.rouz.hpack.primitive.HuffmanDecodingError;

import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.wrap;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(Theories.class)
public class HuffmanCodecTest {

  private static byte[] WWW_EXAMPLE_COM = new byte[] {
      (byte) 0x8c, (byte) 0xf1, (byte) 0xe3, (byte) 0xc2, (byte) 0xe5, (byte) 0xf2, (byte) 0x3a,
      (byte) 0x6b, (byte) 0xa0, (byte) 0xab, (byte) 0x90, (byte) 0xf4, (byte) 0xff
  };

  @DataPoint
  public static HuffmanCodec LOOKUP_TRIE = HuffmanCodec.LOOKUP_TRIE_INSTANCE;

  @DataPoint
  public static HuffmanCodec NAIVE_TRIE = HuffmanCodec.NAIVE_TRIE_INSTANCE;

  @Theory
  public void shouldDecodeStringsFromRFC(HuffmanCodec codec) throws Exception {
    String decoded = new String(codec.decode(wrap(WWW_EXAMPLE_COM)));
    assertThat(decoded, is("www.example.com"));

    // foo=...
    byte[] encoded = new byte[] {
        (byte) 0xad, (byte) 0x94, (byte) 0xe7, (byte) 0x82, (byte) 0x1d, (byte) 0xd7, (byte) 0xf2,
        (byte) 0xe6, (byte) 0xc7, (byte) 0xb3, (byte) 0x35, (byte) 0xdf, (byte) 0xdf, (byte) 0xcd,
        (byte) 0x5b, (byte) 0x39, (byte) 0x60, (byte) 0xd5, (byte) 0xaf, (byte) 0x27, (byte) 0x08,
        (byte) 0x7f, (byte) 0x36, (byte) 0x72, (byte) 0xc1, (byte) 0xab, (byte) 0x27, (byte) 0x0f,
        (byte) 0xb5, (byte) 0x29, (byte) 0x1f, (byte) 0x95, (byte) 0x87, (byte) 0x31, (byte) 0x60,
        (byte) 0x65, (byte) 0xc0, (byte) 0x03, (byte) 0xed, (byte) 0x4e, (byte) 0xe5, (byte) 0xb1,
        (byte) 0x06, (byte) 0x3d, (byte) 0x50, (byte) 0x07
    };

    String decoded2 = new String(codec.decode(wrap(encoded)));
    assertThat(decoded2, is("foo=ASDJKHQKBZXOQWEOPIUAXQWEOIU; max-age=3600; version=1"));
  }

  @Theory
  public void shouldLeaveBufferAtEndOfEncoding(HuffmanCodec codec) throws Exception {
    String message = "www.example.com";

    ByteBuffer buffer = allocate(32);
    int written = codec.encode(message.getBytes(Util.CHARSET), buffer);

    assertThat(buffer.position(), is(written));
  }

  @Theory
  public void shouldLeaveBufferAtEndOfDecoding(HuffmanCodec codec) throws Exception {
    ByteBuffer buffer = wrap(WWW_EXAMPLE_COM);
    codec.decode(buffer);

    assertThat(buffer.position(), is(WWW_EXAMPLE_COM.length));
  }

  @Theory
  public void shouldDecodeEncoded(HuffmanCodec codec) throws Exception {
    String message = "www.example.com";
    byte[] bytes = message.getBytes(Util.CHARSET);

    ByteBuffer buffer = allocate(WWW_EXAMPLE_COM.length);
    int written = codec.encode(bytes, buffer);

    buffer.rewind();
    String decoded = new String(codec.decode(buffer));

    assertThat(written, is(WWW_EXAMPLE_COM.length));
    assertThat(buffer.array(), is(WWW_EXAMPLE_COM));
    assertThat(decoded, is(message));
  }

  @Theory
  public void shouldDecodeEncodedRandomStrings(HuffmanCodec codec) throws Exception {
    Random random = new Random();
    for (int i = 0; i < 10000; i++) {
      // larger number will cause multi-byte size which needs to be implemented
      int len = random.nextInt(42) + 1;
      byte[] bytes = new byte[len];
      random.nextBytes(bytes);

      ByteBuffer buffer = ByteBuffer.allocate(8 * len);
      codec.encode(bytes, buffer);
      buffer.flip();

      final byte[] decoded = codec.decode(buffer);
      assertThat(decoded, is(bytes));
    }
  }

  @Theory
  public void shouldThrowOnNullBufferEncode(HuffmanCodec codec) throws Exception {
    try {
      codec.encode(new byte[4], null);
      fail();
    } catch (NullPointerException e) {
      assertThat(e.getMessage(), containsString("buffer is null"));
    }
  }

  @Theory
  public void shouldThrowOnNullInputEncode(HuffmanCodec codec) throws Exception {
    try {
      codec.encode(null, allocate(4));
      fail();
    } catch (NullPointerException e) {
      assertThat(e.getMessage(), containsString("input is null"));
    }
  }

  @Theory
  public void shouldThrowOnNullBufferDecode(HuffmanCodec codec) throws Exception {
    try {
      codec.decode(null);
      fail();
    } catch (NullPointerException e) {
      assertThat(e.getMessage(), containsString("buffer is null"));
    }
  }

  @Theory
  public void shouldThrowOnZeroRemainingBuffer(HuffmanCodec codec) throws Exception {
    ByteBuffer buffer = ByteBuffer.allocate(16);
    buffer.position(16);

    try {
      codec.decode(buffer);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), containsString("No bytes"));
    }
  }

  @Theory
  public void shouldThrowOnNoTHuffmanEncoded(HuffmanCodec codec) throws Exception {
    ByteBuffer buffer = ByteBuffer.allocate(16);
    buffer.put((byte) 0);
    buffer.rewind();

    try {
      codec.decode(buffer);
      fail();
    } catch (HuffmanDecodingError e) {
      assertThat(e.getMessage(), containsString("Not huffman encoded"));
    }
  }

  // how do we detect this? butes are just discarded as it works right now
//  @Theory
  public void shouldErrorOnLongPadding(HuffmanCodec codec) throws Exception {
    byte[] encoded = new byte[] {
        (byte) 0x8d, (byte) 0xf1, (byte) 0xe3, (byte) 0xc2, (byte) 0xe5, (byte) 0xf2, (byte) 0x3a,
        (byte) 0x6b, (byte) 0xa0, (byte) 0xab, (byte) 0x90, (byte) 0xf4, (byte) 0xff, (byte) 0xff
    };

    try {
      codec.decode(wrap(encoded));
      fail();
    } catch (HuffmanDecodingError e) {
      assertThat(e.getMessage(), containsString("7 bits"));
    }
  }

  @Theory
  public void shouldErrorOnErroneousPadding(HuffmanCodec codec) throws Exception {
    byte[] encoded = new byte[] {
        (byte) 0x8c, (byte) 0xf1, (byte) 0xe3, (byte) 0xc2, (byte) 0xe5, (byte) 0xf2, (byte) 0x3a,
        (byte) 0x6b, (byte) 0xa0, (byte) 0xab, (byte) 0x90, (byte) 0xf4, (byte) 0xfd
    };

    try {
      codec.decode(wrap(encoded));
      fail();
    } catch (HuffmanDecodingError e) {
      assertThat(e.getMessage(), containsString("not correspond to EOS"));
    }
  }
}
