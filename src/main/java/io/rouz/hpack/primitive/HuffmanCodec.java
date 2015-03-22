package io.rouz.hpack.primitive;

import java.nio.ByteBuffer;

/**
 * TODO: document.
 */
public interface HuffmanCodec {

  static final HuffmanCodec INSTANCE =
      new HuffmanCodecImpl(VarInt.INSTANCE, HuffmanCode.CODES, HuffmanCode.CODE_LENGTHS);

  byte[] decode(ByteBuffer buffer) throws HuffmanDecodingError;

  int encode(byte[] input, ByteBuffer buffer);
}
