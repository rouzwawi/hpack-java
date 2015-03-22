package io.rouz;

import io.rouz.hpack.table.StaticTable;

public class HPACK {

  public static void main(String[] args) {
    int i = 2;
    System.out.println("StaticTable.get(i).name() = " + StaticTable.get(i).name());
    System.out.println("StaticTable.get(i).value() = " + StaticTable.get(i).value());
  }


}
