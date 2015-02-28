package io.rouz;

import static io.rouz.VarInt.decode;
import static io.rouz.VarInt.encode;

public class HPACK {

  public static void main(String[] args) {
    byte[] number = new byte[16];
    int bytes = 0;
    bytes = encode(1337, 5, number, bytes);
    bytes = encode(10, 5, number, bytes);
    bytes = encode(42, 8, number, bytes);
    bytes = encode(Integer.MAX_VALUE, 4, number, bytes);

    printBinary(number, bytes);

    System.out.println("decode(5, number, 0) = " + decode(5, number, 0));
    System.out.println("decode(5, number, 3) = " + decode(5, number, 3));
    System.out.println("decode(8, number, 4) = " + decode(8, number, 4));
    System.out.println("decode(4, number, 5) = " + decode(4, number, 5));
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
