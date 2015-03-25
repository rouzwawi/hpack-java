package io.rouz.hpack.primitive;

import java.nio.ByteBuffer;

/**
 * TODO: document.
 */
public interface HuffmanCodec {

  static final HuffmanCodec NAIVE_TRIE_INSTANCE =
      new NaiveTrieHuffmanCodec(VarInt.INSTANCE, HuffmanCode.CODES, HuffmanCode.CODE_LENGTHS);

  static final HuffmanCodec LOOKUP_TRIE_INSTANCE =
      new LookupTrieHuffmanCodec(VarInt.INSTANCE, HuffmanCode.CODES, HuffmanCode.CODE_LENGTHS);

  static final HuffmanCodec INSTANCE = LOOKUP_TRIE_INSTANCE;

  byte[] decode(ByteBuffer buffer) throws HuffmanDecodingError;

  int encode(byte[] input, ByteBuffer buffer);
}
