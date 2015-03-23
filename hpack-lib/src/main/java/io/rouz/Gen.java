package io.rouz;

public class Gen {

  public static void main(String[] args) {
    for (int i = 0x80; i <= 0xff;) {
      for (int n = 0; n < 4; n++) {
        String x = String.format("%02X", i).toLowerCase();
        System.out.print("Ox" + x + " = (byte) 0x" + x + ", ");
        i++;
      }
      System.out.println();
    }
  }
}
