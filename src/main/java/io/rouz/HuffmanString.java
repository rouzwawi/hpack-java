package io.rouz;

/**
 * TODO: document.
 */
public interface HuffmanString {

  static final HuffmanString INSTANCE =
      new HPackStringImpl(VarInt.INSTANCE, HuffmanCode.CODES, HuffmanCode.CODE_LENGTHS);

  String decode(byte[] encoded) throws HuffmanDecodingError;

  String decode(byte[] encoded, int pos) throws HuffmanDecodingError;

  byte[] encode(String input);
}
