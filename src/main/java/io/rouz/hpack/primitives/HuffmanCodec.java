package io.rouz.hpack.primitives;

/**
 * TODO: document.
 */
public interface HuffmanCodec {

  static final HuffmanCodec INSTANCE =
      new HuffmanCodecImpl(VarInt.INSTANCE, HuffmanCode.CODES, HuffmanCode.CODE_LENGTHS);

  byte[] decode(byte[] encoded) throws HuffmanDecodingError;

  byte[] decode(byte[] encoded, int pos) throws HuffmanDecodingError;

  byte[] encode(byte[] input);
}
