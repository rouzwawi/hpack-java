package io.rouz.hpack.primitive;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * TODO: document.
 */
public class LookupTrieHuffmanCodec implements HuffmanCodec {

  private final VarInt varInt;
  private final int[] codes;
  private final byte[] codeLengths;

  private final Node rootTable;

  LookupTrieHuffmanCodec(final VarInt varInt, int[] codes, byte[] codeLengths) {
    this.varInt = varInt;
    this.codes = codes;
    this.codeLengths = codeLengths;

    rootTable = constructTable();
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

    final int totalBytes = varInt.decode(7, buffer);

    Node node = rootTable;

    int readBytes = 0;
    int off = 0;
    int reg = 0;

    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    while (readBytes < totalBytes) {
      int b = buffer.get() & 0xff;
      readBytes++;
      reg = (reg << 8) | b;
      off += 8;

      while (off >= 8) {
        node = node.children[((reg >>> (off - 8)) & 0xff)];

        if (node.leaf()) {
          output.write(node.symbol);
          off -= node.bits;
          node = rootTable;
        } else {
          off -= 8;
        }
      }
    }

    int tail = off;
    while (tail > 0) {
      reg = (reg << (8 - off));
      node = node.children[reg & 0xff];

      if (!node.leaf() || node.bits > tail) {
        break;
      }

      output.write(node.symbol);
      off -= node.bits - (8 - off);
      tail -= node.bits;
      node = rootTable;
    }

    final int mask = (0xff << (8 - tail)) & 0xff;
    if ((reg & mask) != mask) {
      throw new HuffmanDecodingError("Padding bits do not correspond to EOS");
    }

    if (tail > 7) {
      throw new HuffmanDecodingError("Padding longer than 7 bits found");
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

  private Node constructTable() {
    Node node = new Node();

    for (int i = 0; i < codes.length; i++) {
      final int code = codes[i];
      final int codeLen = codeLengths[i];

      insert(node, i, code, codeLen);
    }

    return node;
  }

  private static void insert(Node node, int symbol, int code, int len) {
    if (len <= 8) {
      final Node leaf = new Node(symbol, len);
      final int fill = 8 - len;
      final int octet = (code << fill) & 0xff;
      for (int i = 0; i < (1 << fill); i++) {
        node.children[octet | i] = leaf;
      }
      return;
    }


    final int octet = (code >>> (len - 8)) & 0xff;
    if (node.children[octet] == null) {
      node.children[octet] = new Node();
    }

    insert(node.children[octet], symbol, code, len - 8);
  }

  private static final class Node {
    final Node[] children;
    final int symbol;
    final int bits;

    private Node() {
      this.children =  new Node[256];
      this.symbol = 0;
      this.bits = 8;
    }

    private Node(int symbol, int bits) {
      this.children = null;
      this.symbol = symbol;
      this.bits = bits;
    }

    boolean leaf() {
      return children == null;
    }
  }

}
