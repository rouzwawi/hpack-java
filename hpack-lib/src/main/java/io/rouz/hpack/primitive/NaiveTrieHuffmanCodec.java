package io.rouz.hpack.primitive;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * TODO: document.
 */
final class NaiveTrieHuffmanCodec implements HuffmanCodec {

  private final VarInt varInt;
  private final int[] codes;
  private final byte[] codeLengths;

  private final HNode treeRoot;

  NaiveTrieHuffmanCodec(final VarInt varInt, int[] codes, byte[] codeLengths) {
    this.varInt = varInt;
    this.codes = codes;
    this.codeLengths = codeLengths;

    treeRoot = constructTree();
  }

  @Override
  public byte[] decode(ByteBuffer buffer) throws HuffmanDecodingError {
    if (buffer == null) {
      throw new NullPointerException("buffer is null");
    }

    if (buffer.remaining() == 0) {
      throw new IllegalArgumentException("No bytes remaining in buffer");
    }

    if ((buffer.get(buffer.position()) & 0x80) == 0) {
      throw new HuffmanDecodingError("Not huffman encoded string in buffer");
    }

    final int stringLength = varInt.decode(7, buffer);

    HNode node = treeRoot;
    int mask = 0;
    int depth = 0;
    boolean allOnes = true;

    int readBytes = 0;
    byte current = 0;

    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    do {
      if (mask == 0) {
        mask = 0x80;
        current = buffer.get();
        readBytes++;
      }
      final boolean b = (current & mask) != 0;

      depth++;
      allOnes &= b;

      node = b ? node.c1 : node.c0;

      if (node.leaf()) {
        output.write(node.symbol);
        node = treeRoot;
        depth = 0;
        allOnes = true;
      }

      mask >>= 1;
    } while (mask != 0 || readBytes < stringLength);

    if (depth > 7) {
      throw new HuffmanDecodingError("Padding longer than 7 bits found");
    }

    if (!allOnes) {
      throw new HuffmanDecodingError("Padding bits do not correspond to EOS");
    }

    return output.toByteArray();
  }

  @Override
  public int encode(byte[] input, ByteBuffer buffer) {
    if (input == null) {
      throw new NullPointerException("input is null");
    }

    if (buffer == null) {
      throw new NullPointerException("buffer is null");
    }

    final int initial = buffer.position();

    // TODO fix for multi byte sizes
    // placeholder for length byte, with huffman bit set, 7 bit prefix int written later
    buffer.put((byte) 0x80);

    long code = 0;
    int bits = 0;

    for (int i = 0; i < input.length; i++) {
      int b = input[i] & 0xff;

      final byte len = codeLengths[b];
      code <<= len;
      code |= codes[b];
      bits += len;

      while (bits >= 8) {
        bits -= 8;
        buffer.put((byte) ((code >> bits) & 0xff));
      }
    }

    if (bits > 0) {
      code <<= 8 - bits;
      code |= 0xff >> bits;
      buffer.put((byte) (code & 0xff));
    }

    final int last = buffer.position();
    final int bytesWritten = last - initial;
    buffer.position(initial);

    // length
    varInt.encode(bytesWritten - 1, 7, buffer);

    buffer.position(last);
    return bytesWritten;
  }

  private HNode constructTree() {
    HNode node = null;

    for (int i = 0; i < codes.length; i++) {
      int code = codes[i];
      int codeLen = codeLengths[i];

      node = insert(node, (char) i, code, codeLen, 0);
    }

    return node;
  }

  private static HNode insert(HNode node, char symbol, int code, int len, int pos) {
    if (pos == len) {
      return new HNode(symbol);
    }

    final boolean b = (code & (1 << (len - 1 - pos))) != 0;

    final HNode branch =
        node != null
            ? b ? node.c1 : node.c0
            : null;
    final HNode insert = insert(branch, symbol, code, len, pos + 1);

    if (b) {
      return new HNode(node != null ? node.c0 : null, insert);
    } else {
      return new HNode(insert, node != null ? node.c1 : null);
    }
  }

  // TODO: re-implement as prefix table
  private static final class HNode {
    final HNode c0, c1;
    final char symbol;

    private HNode(HNode c0, HNode c1) {
      this.c0 = c0;
      this.c1 = c1;
      this.symbol = 0;
    }

    private HNode(char symbol) {
      this.c0 = this.c1 = null;
      this.symbol = symbol;
    }

    boolean leaf() {
      return c0 == null && c1 == null;
    }
  }
}
