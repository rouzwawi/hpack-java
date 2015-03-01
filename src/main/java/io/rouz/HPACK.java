package io.rouz;

public class HPACK {

  public static void main(String[] args) {
    final VarInt varInt = VarIntImpl.INSTANCE;
    final HuffmanString hstring = HPackStringImpl.INSTANCE;

    byte[] number = new byte[16];
    int bytes = 0;
    bytes = varInt.encode(1337, 5, number, bytes);
    bytes = varInt.encode(10, 5, number, bytes);
    bytes = varInt.encode(42, 8, number, bytes);
    bytes = varInt.encode(Integer.MAX_VALUE, 6, number, bytes);

    printBinary(number, bytes);

    System.out.println("decode(5, number, 0) = " + varInt.decode(5, number, 0));
    System.out.println("decode(5, number, 3) = " + varInt.decode(5, number, 3));
    System.out.println("decode(8, number, 4) = " + varInt.decode(8, number, 4));
    System.out.println("decode(4, number, 5) = " + varInt.decode(4, number, 5));

    System.out.println();

    byte[] huffmanEncoded = new byte[] {
        (byte) 0x8c, (byte) 0xf1, (byte) 0xe3, (byte) 0xc2, (byte) 0xe5, (byte) 0xf2, (byte) 0x3a,
        (byte) 0x6b, (byte) 0xa0, (byte) 0xab, (byte) 0x90, (byte) 0xf4, (byte) 0xff
    };

    String huffmanDecoded = hstring.decode(huffmanEncoded);
    System.out.println("huffmanDecoded = " + huffmanDecoded);

    byte[] h2 = new byte[] {
        (byte) 0xad, (byte) 0x94, (byte) 0xe7, (byte) 0x82, (byte) 0x1d, (byte) 0xd7, (byte) 0xf2,
        (byte) 0xe6, (byte) 0xc7, (byte) 0xb3, (byte) 0x35, (byte) 0xdf, (byte) 0xdf, (byte) 0xcd,
        (byte) 0x5b, (byte) 0x39, (byte) 0x60, (byte) 0xd5, (byte) 0xaf, (byte) 0x27, (byte) 0x08,
        (byte) 0x7f, (byte) 0x36, (byte) 0x72, (byte) 0xc1, (byte) 0xab, (byte) 0x27, (byte) 0x0f,
        (byte) 0xb5, (byte) 0x29, (byte) 0x1f, (byte) 0x95, (byte) 0x87, (byte) 0x31, (byte) 0x60,
        (byte) 0x65, (byte) 0xc0, (byte) 0x03, (byte) 0xed, (byte) 0x4e, (byte) 0xe5, (byte) 0xb1,
        (byte) 0x06, (byte) 0x3d, (byte) 0x50, (byte) 0x07
    };

    huffmanDecoded = hstring.decode(h2);
    System.out.println("huffmanDecoded = " + huffmanDecoded);
  }

  private static void printBinary(byte[] number, int bytes) {
    for (int i = 0; i < bytes; i++) {
      printBinary(number[i]);
    }
  }

  private static void printBinary(final byte b) {
    int mask = 0x80;
    for (int i = 0; i < 8; i++) {
      System.out.print((b & mask) != 0 ? '1' : '0');
      mask >>= 1;
    }
    System.out.println(" | " + String.format("%02X", b).toLowerCase());
  }


}
