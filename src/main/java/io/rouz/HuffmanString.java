package io.rouz;

/**
 * TODO: document.
 */
public interface HuffmanString {

  static final HuffmanString INSTANCE = new HPackStringImpl(VarInt.INSTANCE);

  String decode(byte[] encoded);

  String decode(byte[] encoded, int pos);
}
