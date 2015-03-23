package io.rouz.hpack.field;

final class LiteralHeaderName implements HeaderName {

  private final String name;

  LiteralHeaderName(String name) {
    this.name = name;
  }

  @Override
  public boolean indexed() {
    return false;
  }

  @Override
  public int index() {
    throw new UnsupportedOperationException("index() on non-indexed name");
  }

  @Override
  public String literalName() {
    return name;
  }
}
