package io.rouz.hpack.field;

import java.nio.charset.Charset;
import java.util.Optional;

import io.rouz.hpack.primitive.HuffmanCodec;
import io.rouz.hpack.primitive.VarInt;

import static io.rouz.hpack.field.HeaderFieldCollectors.index;
import static io.rouz.hpack.field.HeaderFieldCollectors.name;
import static io.rouz.hpack.field.HeaderFieldCollectors.value;
import static java.lang.System.arraycopy;

/**
 * TODO: document.
 */
public interface HeaderFieldCodec {

  public static final HeaderFieldCodec INSTANCE =
      new Impl(VarInt.INSTANCE, HuffmanCodec.INSTANCE);

  int encode(HeaderField headerField, byte[] buffer);

  static class Impl implements HeaderFieldCodec {

    private final Charset CHARSET = Charset.forName("ISO-8859-1");

    private final VarInt varInt;
    private final HuffmanCodec huffmanCodec;

    public Impl(VarInt varInt, HuffmanCodec huffmanCodec) {
      this.varInt = varInt;
      this.huffmanCodec = huffmanCodec;
    }

    @Override
    public int encode(HeaderField headerField, byte[] buffer) {
      final HeaderFieldType type = headerField.type();

      int pos = 0;
      buffer[pos] = (byte) type.pattern();

      final int index = index(headerField).orElse(0);
      varInt.encode(index, 8 - type.bits(), buffer, pos);
      pos++;

      final Optional<String> name = name(headerField);
      if (name.isPresent()) {
        // TODO: option for disabling huffman encoding
        final byte[] encodedName = huffmanCodec.encode(name.get().getBytes(CHARSET));
        arraycopy(encodedName, 0, buffer, pos, encodedName.length);
        pos += encodedName.length;
      }

      final Optional<String> value = value(headerField);
      if (value.isPresent()) {
        // TODO: option for disabling huffman encoding
        final byte[] encodedValue = huffmanCodec.encode(value.get().getBytes(CHARSET));
        arraycopy(encodedValue, 0, buffer, pos, encodedValue.length);
        pos += encodedValue.length;
      }

      return pos;
    }
  }

}
