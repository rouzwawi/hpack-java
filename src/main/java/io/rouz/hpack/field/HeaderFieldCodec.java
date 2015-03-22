package io.rouz.hpack.field;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Optional;

import io.rouz.hpack.primitive.HuffmanCodec;
import io.rouz.hpack.primitive.VarInt;

import static io.rouz.hpack.field.HeaderFieldCollectors.index;
import static io.rouz.hpack.field.HeaderFieldCollectors.name;
import static io.rouz.hpack.field.HeaderFieldCollectors.value;

/**
 * TODO: document.
 */
public interface HeaderFieldCodec {

  public static final HeaderFieldCodec INSTANCE =
      new Impl(VarInt.INSTANCE, HuffmanCodec.INSTANCE);

  int encode(HeaderField headerField, ByteBuffer buffer);

  static class Impl implements HeaderFieldCodec {

    private final Charset CHARSET = Charset.forName("ISO-8859-1");

    private final VarInt varInt;
    private final HuffmanCodec huffmanCodec;

    public Impl(VarInt varInt, HuffmanCodec huffmanCodec) {
      this.varInt = varInt;
      this.huffmanCodec = huffmanCodec;
    }

    @Override
    public int encode(HeaderField headerField, ByteBuffer buffer) {
      final HeaderFieldType type = headerField.type();

      int bytes = 0;
      buffer.put(buffer.position(), (byte) type.pattern());

      final int index = index(headerField).orElse(0);
      bytes += varInt.encode(index, 8 - type.bits(), buffer);

      final Optional<String> name = name(headerField);
      if (name.isPresent()) {
        // TODO: option for disabling huffman encoding
        bytes += huffmanCodec.encode(name.get().getBytes(CHARSET), buffer);
      }

      final Optional<String> value = value(headerField);
      if (value.isPresent()) {
        // TODO: option for disabling huffman encoding
        bytes += huffmanCodec.encode(value.get().getBytes(CHARSET), buffer);
      }

      return bytes;
    }
  }

}
