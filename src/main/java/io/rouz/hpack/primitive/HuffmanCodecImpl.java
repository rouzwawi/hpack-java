package io.rouz.hpack.primitive;

import java.io.ByteArrayOutputStream;

final class HuffmanCodecImpl implements HuffmanCodec {

  private final VarInt varInt;
  private final int[] codes;
  private final byte[] codeLengths;

  private final HNode treeRoot;

  HuffmanCodecImpl(final VarInt varInt, int[] codes, byte[] codeLengths) {
    this.varInt = varInt;
    this.codes = codes;
    this.codeLengths = codeLengths;

    treeRoot = constructTree();
  }

  @Override
  public byte[] decode(byte[] encoded) throws HuffmanDecodingError {
    return decode(encoded, 0);
  }

  @Override
  public byte[] decode(byte[] encoded, int pos) throws HuffmanDecodingError {
    if (encoded.length == 0 || (encoded[0] & 0x80) == 0) {
      throw new IllegalArgumentException("No bytes or not huffman encoded string");
    }

    final int stringLength = varInt.decode(7, encoded, pos);

    HNode node = treeRoot;
    int mask = 0x80;
    int depth = 0;
    boolean allOnes = true;

    int off = pos + 1;

    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    while (off - pos - 1 < stringLength) {
      final boolean b = (encoded[off] & mask) != 0;

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
      if (mask == 0) {
        mask = 0x80;
        off++;
      }
    }

    if (depth > 7) {
      throw new HuffmanDecodingError("Padding longer than 7 bits found");
    }

    if (!allOnes) {
      throw new HuffmanDecodingError("Padding bits do not correspond to EOS");
    }

    return output.toByteArray();
  }

  @Override
  public byte[] encode(byte[] input) {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();

    // placeholder for length byte
    output.write(0);

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
        output.write((int) (code >> bits));
      }
    }

    if (bits > 0) {
      code <<= 8 - bits;
      code |= 0xff >> bits;
      output.write((int) code);
    }

    final byte[] bytes = output.toByteArray();

    // huffman bit set
    bytes[0] |= 0x80;

    // length
    varInt.encode(bytes.length - 1, 7, bytes);

    return bytes;
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
