package io.rouz.hpack.field;

/**
* A struct holding the name/value pair in the dynamic and static tables.
*/
public class HeaderField {
  private final String name;
  private final String value;

  private HeaderField(String name) {
    this(name, null);
  }

  private HeaderField(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String name() {
    return name;
  }

  public String value() {
    return value;
  }

  public static HeaderField create(String name, String value) {
    return new HeaderField(name, value);
  }

  public static HeaderField create(String name) {
    return new HeaderField(name);
  }
}
