package io.rouz.hpack;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import io.rouz.hpack.primitive.HuffmanCodec;
import io.rouz.hpack.primitive.HuffmanDecodingError;

import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.wrap;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class HuffmanCodecTest {

  private static final Charset CHARSET = Charset.forName("ISO-8859-1");

  private static byte[] WWW_EXAMPLE_COM = new byte[] {
      (byte) 0x8c, (byte) 0xf1, (byte) 0xe3, (byte) 0xc2, (byte) 0xe5, (byte) 0xf2, (byte) 0x3a,
      (byte) 0x6b, (byte) 0xa0, (byte) 0xab, (byte) 0x90, (byte) 0xf4, (byte) 0xff
  };

  HuffmanCodec hstring = HuffmanCodec.INSTANCE;

  @Test
  public void shouldDecodeStringsFromRFC() throws Exception {
    String decoded = new String(hstring.decode(wrap(WWW_EXAMPLE_COM)));
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

    decoded = new String(hstring.decode(wrap(encoded)));
    assertThat(decoded, is("foo=ASDJKHQKBZXOQWEOPIUAXQWEOIU; max-age=3600; version=1"));
  }

  @Test
  public void shouldLeaveBufferAtEndOfEncoding() throws Exception {
    String message = "www.example.com";

    ByteBuffer buffer = allocate(32);
    int written = hstring.encode(message.getBytes(CHARSET), buffer);

    assertThat(buffer.position(), is(written));
  }

  @Test
  public void shouldLeaveBufferAtEndOfDecoding() throws Exception {
    ByteBuffer buffer = wrap(WWW_EXAMPLE_COM);
    hstring.decode(buffer);

    assertThat(buffer.position(), is(WWW_EXAMPLE_COM.length));
  }

  @Test
  public void shouldDecodeEncoded() throws Exception {
    String message = "www.example.com";
    byte[] bytes = message.getBytes(CHARSET);

    ByteBuffer buffer = allocate(WWW_EXAMPLE_COM.length);
    int written = hstring.encode(bytes, buffer);

    buffer.rewind();
    String decoded = new String(hstring.decode(buffer));

    assertThat(written, is(WWW_EXAMPLE_COM.length));
    assertThat(buffer.array(), is(WWW_EXAMPLE_COM));
    assertThat(decoded, is(message));
  }

  @Test
  public void shouldThrowOnNullBufferEncode() throws Exception {
    try {
      hstring.encode(new byte[4], null);
      fail();
    } catch (NullPointerException e) {
      assertThat(e.getMessage(), containsString("buffer is null"));
    }
  }

  @Test
  public void shouldThrowOnNullInputEncode() throws Exception {
    try {
      hstring.encode(null, allocate(4));
      fail();
    } catch (NullPointerException e) {
      assertThat(e.getMessage(), containsString("input is null"));
    }
  }

  @Test
  public void shouldThrowOnNullBufferDecode() throws Exception {
    try {
      hstring.decode(null);
      fail();
    } catch (NullPointerException e) {
      assertThat(e.getMessage(), containsString("buffer is null"));
    }
  }

  @Test
  public void shouldThrowOnZeroRemainingBuffer() throws Exception {
    ByteBuffer buffer = ByteBuffer.allocate(16);
    buffer.position(16);

    try {
      hstring.decode(buffer);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage(), containsString("No bytes"));
    }
  }

  @Test
  public void shouldThrowOnNoTHuffmanEncoded() throws Exception {
    ByteBuffer buffer = ByteBuffer.allocate(16);
    buffer.put((byte) 0);
    buffer.rewind();

    try {
      hstring.decode(buffer);
      fail();
    } catch (HuffmanDecodingError e) {
      assertThat(e.getMessage(), containsString("Not huffman encoded"));
    }
  }

  @Test
  public void shouldErrorOnLongPadding() throws Exception {
    byte[] encoded = new byte[] {
        (byte) 0x8d, (byte) 0xf1, (byte) 0xe3, (byte) 0xc2, (byte) 0xe5, (byte) 0xf2, (byte) 0x3a,
        (byte) 0x6b, (byte) 0xa0, (byte) 0xab, (byte) 0x90, (byte) 0xf4, (byte) 0xff, (byte) 0xff
    };

    try {
      hstring.decode(wrap(encoded));
      fail();
    } catch (HuffmanDecodingError e) {
      assertThat(e.getMessage(), containsString("7 bits"));
    }
  }

  @Test
  public void shouldErrorOnErroneousPadding() throws Exception {
    byte[] encoded = new byte[] {
        (byte) 0x8c, (byte) 0xf1, (byte) 0xe3, (byte) 0xc2, (byte) 0xe5, (byte) 0xf2, (byte) 0x3a,
        (byte) 0x6b, (byte) 0xa0, (byte) 0xab, (byte) 0x90, (byte) 0xf4, (byte) 0xfd
    };

    try {
      hstring.decode(wrap(encoded));
      fail();
    } catch (HuffmanDecodingError e) {
      assertThat(e.getMessage(), containsString("not correspond to EOS"));
    }
  }
}
