package io.rouz;

public final class Util {

  private Util() {}

  public static void printBinary(byte[] number, int bytes) {
    for (int i = 0; i < bytes; i++) {
      printBinary(number[i]);
    }
  }

  public static void printBinary(final byte b) {
    int mask = 0x80;
    for (int i = 0; i < 8; i++) {
      System.out.print((b & mask) != 0 ? '1' : '0');
      mask >>= 1;
    }
    System.out.println(" | " + String.format("%02X", b).toLowerCase());
  }
}
