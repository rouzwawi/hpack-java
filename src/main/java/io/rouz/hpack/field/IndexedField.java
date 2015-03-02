package io.rouz.hpack.field;

import com.google.common.base.Preconditions;

import static io.rouz.hpack.field.HeaderField.IndexedName;

final class IndexedField extends AbstractHeaderField
    implements IndexedName {

  private final HeaderName name;

  IndexedField(HeaderName name) {
    super(HeaderFieldType.INDEXED);

    Preconditions.checkArgument(name.indexed(), "name is not indexed");
    this.name = name;
  }

  @Override
  public <T extends HeaderFieldVisitor> T visit(T visitor) {
    visitor.indexedName(this, true);
    return visitor;
  }

  @Override
  public int index() {
    return name.index();
  }
}
