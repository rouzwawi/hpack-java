package io.rouz;

final class HPackStringImpl implements HuffmanString {

  private final HNode CODE_TREE = readCode();

  private final VarInt varInt;

  HPackStringImpl(final VarInt varInt) {
    this.varInt = varInt;
  }

  @Override
  public String decode(byte[] encoded) throws HuffmanDecodingError {
    return decode(encoded, 0);
  }

  @Override
  public String decode(byte[] encoded, int pos) throws HuffmanDecodingError {
    if (encoded.length == 0 || (encoded[0] & 0x80) == 0) {
      throw new IllegalArgumentException("No bytes or not huffman encoded string");
    }

    final int stringLength = varInt.decode(7, encoded, pos);

    HNode node = CODE_TREE;
    int mask = 0x80;
    int depth = 0;
    boolean allOnes = true;

    int off = pos + 1;

    final StringBuilder sb = new StringBuilder();
    while (off - pos - 1 < stringLength) {
      final boolean b = (encoded[off] & mask) != 0;

      depth++;
      allOnes &= b;

      node = b ? node.c1 : node.c0;

      if (node.leaf()) {
        sb.append(node.symbol);
        node = CODE_TREE;
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

    return sb.toString();
  }

  private static HNode readCode() {
    final int[] codes = HuffmanCode.CODES;
    final byte[] codeLengths = HuffmanCode.CODE_LENGTHS;

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
