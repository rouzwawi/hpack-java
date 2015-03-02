package io.rouz.hpack.field;

abstract class AbstractHeaderFieldImpl implements HeaderField {

  private final HeaderFieldType type;

  AbstractHeaderFieldImpl(HeaderFieldType type) {
    this.type = type;
  }

  @Override
  public HeaderFieldType type() {
    return type;
  }
}
