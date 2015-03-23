package io.rouz.hpack.field;

import io.rouz.hpack.Util;

/**
* A struct holding the name/value pair in the dynamic and static tables.
*/
public class HeaderField {

  private static final String EMPTY = "";
  private static final int SIZE_OVERHEAD = 32;

  private final String name;
  private final String value;

  private final byte[] nameBytes;
  private final byte[] valueBytes;

  private HeaderField(String name) {
    this(name, null);
  }

  private HeaderField(String name, String value) {
    this.name = name;
    this.value = value != null ? value : EMPTY;

    this.nameBytes = this.name.getBytes(Util.CHARSET);
    this.valueBytes = this.value.getBytes(Util.CHARSET);
  }

  public String name() {
    return name;
  }

  public String value() {
    return value;
  }

  public int size() {
    return nameBytes.length + valueBytes.length + SIZE_OVERHEAD;
  }

  public static HeaderField create(String name, String value) {
    return new HeaderField(name, value);
  }

  public static HeaderField create(String name) {
    return new HeaderField(name);
  }
}
