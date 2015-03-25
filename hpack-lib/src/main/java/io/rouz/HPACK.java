package io.rouz;

import io.rouz.hpack.table.StaticTable;

public class HPACK {

  public static void main(String[] args) {

    System.out.println("b() & 0xff = " + i());

    int i = 2;
    System.out.println("StaticTable.get(i).name() = " + StaticTable.get(i).name());
    System.out.println("StaticTable.get(i).value() = " + StaticTable.get(i).value());
  }

  private static byte b() {
    return (byte) 255;
  }

  private static int i() {
    return b() & 0xff;
  }

}
