package io.rouz.hpack.field;

abstract class AbstractHeaderFieldRepresentation implements HeaderFieldRepresentation {

  private final HeaderFieldType type;

  AbstractHeaderFieldRepresentation(HeaderFieldType type) {
    this.type = type;
  }

  @Override
  public HeaderFieldType type() {
    return type;
  }
}
