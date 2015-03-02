package io.rouz.hpack.field;

final class IndexedHeaderName implements HeaderName {

  private final int index;

  IndexedHeaderName(int index) {
    this.index = index;
  }

  @Override
  public boolean indexed() {
    return true;
  }

  @Override
  public int index() {
    return index;
  }

  @Override
  public String literalName() {
    throw new UnsupportedOperationException("literalName() on non-literal name");
  }
}
