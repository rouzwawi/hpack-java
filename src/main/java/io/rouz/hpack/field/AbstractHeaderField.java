package io.rouz.hpack.field;

abstract class AbstractHeaderField implements HeaderField {

  private final HeaderFieldType type;

  AbstractHeaderField(HeaderFieldType type) {
    this.type = type;
  }

  @Override
  public HeaderFieldType type() {
    return type;
  }
}
