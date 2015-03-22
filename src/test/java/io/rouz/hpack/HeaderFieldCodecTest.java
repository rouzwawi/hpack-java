package io.rouz.hpack;

import org.junit.Test;

import java.nio.ByteBuffer;

import io.rouz.hpack.field.HeaderField;
import io.rouz.hpack.field.HeaderFieldCodec;
import io.rouz.hpack.field.HeaderFields;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HeaderFieldCodecTest {

  final HeaderFieldCodec headerFieldCodec = HeaderFieldCodec.INSTANCE;

  byte[] wwwBytes = new byte[] {
      (byte) 0x41, (byte) 0x8c, (byte) 0xf1, (byte) 0xe3, (byte) 0xc2, (byte) 0xe5, (byte) 0xf2,
      (byte) 0x3a, (byte) 0x6b, (byte) 0xa0, (byte) 0xab, (byte) 0x90, (byte) 0xf4, (byte) 0xff
  };

  byte[] customBytes = new byte[] {
      (byte) 0x40, (byte) 0x88, (byte) 0x25, (byte) 0xa8, (byte) 0x49, (byte) 0xe9, (byte) 0x5b,
      (byte) 0xa9, (byte) 0x7d, (byte) 0x7f, (byte) 0x89, (byte) 0x25, (byte) 0xa8, (byte) 0x49,
      (byte) 0xe9, (byte) 0x5b, (byte) 0xb8, (byte) 0xe8, (byte) 0xb4, (byte) 0xbf
  };

  final ByteBuffer buffer = ByteBuffer.allocate(32);

  @Test
  public void shouldEncodeIndexedField() throws Exception {
    final HeaderField headerField =
        HeaderFields.indexedField(63);

    int bytes = headerFieldCodec.encode(headerField, buffer);

    byte[] expected = new byte[] { (byte) 0xbf };
    verifyBytes(bytes, buffer, expected);
  }

  @Test
  public void shouldEncodeLiteralFieldWithIndexedName() throws Exception {
    final HeaderField headerField =
        HeaderFields.indexedField(HeaderFields.name(1), "www.example.com");

    int bytes = headerFieldCodec.encode(headerField, buffer);

    verifyBytes(bytes, buffer, wwwBytes);
  }

  @Test
  public void shouldEncodeLiteralFieldWithLiteralName() throws Exception {
    final HeaderField headerField =
        HeaderFields.indexedField(HeaderFields.name("custom-key"), "custom-value");

    int bytes = headerFieldCodec.encode(headerField, buffer);

    verifyBytes(bytes, buffer, customBytes);
  }

  @Test
  public void shouldEncodeNonIndexedLiteralFieldWithIndexedName() throws Exception {
    final HeaderField headerField =
        HeaderFields.nonIndexedField(HeaderFields.name(1), "www.example.com");

    int bytes = headerFieldCodec.encode(headerField, buffer);

    wwwBytes[0] &= ~0xf0;
    verifyBytes(bytes, buffer, wwwBytes);
  }

  @Test
  public void shouldEncodeNonIndexedLiteralFieldWithLiteralName() throws Exception {
    final HeaderField headerField =
        HeaderFields.nonIndexedField(HeaderFields.name("custom-key"), "custom-value");

    int bytes = headerFieldCodec.encode(headerField, buffer);

    customBytes[0] &= ~0xf0;
    verifyBytes(bytes, buffer, customBytes);
  }

  @Test
  public void shouldEncodeNeverIndexedLiteralFieldWithIndexedName() throws Exception {
    final HeaderField headerField =
        HeaderFields.neverIndexedField(HeaderFields.name(1), "www.example.com");

    int bytes = headerFieldCodec.encode(headerField, buffer);

    wwwBytes[0] &= ~0xe0;
    wwwBytes[0] |=  0x10;
    verifyBytes(bytes, buffer, wwwBytes);
  }

  @Test
  public void shouldEncodeNeverIndexedLiteralFieldWithLiteralName() throws Exception {
    final HeaderField headerField =
        HeaderFields.neverIndexedField(HeaderFields.name("custom-key"), "custom-value");

    int bytes = headerFieldCodec.encode(headerField, buffer);

    customBytes[0] &= ~0xe0;
    customBytes[0] |=  0x10;
    verifyBytes(bytes, buffer, customBytes);
  }

  private void verifyBytes(int len, ByteBuffer buffer, byte[] expected) {
    assertThat(len, is(expected.length));
    for (int i = 0; i < expected.length; i++) {
      assertThat("byte " + i, buffer.get(i), is(expected[i]));
    }
  }
}
