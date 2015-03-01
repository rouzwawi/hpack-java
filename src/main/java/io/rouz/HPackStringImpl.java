package io.rouz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

final class HPackStringImpl implements HuffmanString {

  private static final String HUFFMAN_CODE_FILE = "huffman.code";

  private static final byte Oxff = (byte) 0xff;
  private static final byte[] EOS = new byte[] {Oxff, Oxff, Oxff, 0x3f};

  private final HNode CODE_TREE = readCode();
  private final byte[][] CODE_WORDS = new byte[256][];

  private final VarInt varInt;

  HPackStringImpl(final VarInt varInt) {
    this.varInt = varInt;
  }

  @Override
  public String decode(byte[] encoded) {
    return decode(encoded, 0);
  }

  @Override
  public String decode(byte[] encoded, int pos) {
    if (encoded.length == 0 || (encoded[0] & 0x80) == 0) {
      throw new IllegalArgumentException("No bytes or not huffman encoded string");
    }

    HNode node = CODE_TREE;
    final int stringLength = varInt.decode(7, encoded, pos);
    int mask = 0x80;

    pos++;

    final StringBuilder sb = new StringBuilder();
    while (pos < encoded.length) {
      node = (encoded[pos] & mask) == 0
          ? node.c0
          : node.c1;

      if (node.leaf()) {
        sb.append(node.codepoint);
        node = CODE_TREE;
      }

      mask >>= 1;
      if (mask == 0) {
        mask = 0x80;
        pos++;
      }
    }
    return sb.toString();
  }

  private static HNode readCode() {
    final ClassLoader classLoader = HPackStringImpl.class.getClassLoader();
    final InputStream codeStream = classLoader.getResourceAsStream(HUFFMAN_CODE_FILE);
    final BufferedReader in = new BufferedReader(new InputStreamReader(codeStream));

    HNode node = null;

    int lineno = 0;
    try {
      char codepoint = 0;
      String line = null;

      while ((line = in.readLine()) != null) {
        lineno++;
        line = line.trim();
        if (line.isEmpty()) continue;
        if (line.charAt(0) == '#') continue;

        node = insert(node, codepoint, line);
        codepoint++;
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load " + HUFFMAN_CODE_FILE, e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Error parsing huffman file on line " + lineno, e);
    }

    return node;
  }

  private static HNode insert(HNode node, char codepoint, String line)
      throws IllegalArgumentException {
    return insert(node, codepoint, line, 0);
  }

  private static HNode insert(HNode node, char codepoint, String line, int pos)
      throws IllegalArgumentException {
    if (pos == line.length()) {
      return new HNode(codepoint);
    }

    final char b = line.charAt(pos);
    if (b != '0' && b != '1') {
      throw new IllegalArgumentException("Invalid character '" + b + "'");
    }

    final HNode branch =
        node != null
            ? b == '0' ? node.c0 : node.c1
            : null;
    final HNode insert = insert(branch, codepoint, line, pos + 1);

    if (b == '0') {
      return new HNode(insert, node != null ? node.c1 : null);
    } else {
      return new HNode(node != null ? node.c0 : null, insert);
    }
  }

  public static void call() {
  }

  private static final class HNode {
    final HNode c0, c1;
    final char codepoint;

    private HNode(HNode c0, HNode c1) {
      this.c0 = c0;
      this.c1 = c1;
      this.codepoint = 0;
    }

    private HNode(char codepoint) {
      this.c0 = this.c1 = null;
      this.codepoint = codepoint;
    }

    boolean leaf() {
      return c0 == null && c1 == null;
    }
  }
}
