package io.rouz;

/**
 * TODO: document.
 */
public interface HuffmanString {

  static final HuffmanString INSTANCE = new HPackStringImpl(VarInt.INSTANCE);

  String decode(byte[] encoded) throws HuffmanDecodingError;

  String decode(byte[] encoded, int pos) throws HuffmanDecodingError;
}
