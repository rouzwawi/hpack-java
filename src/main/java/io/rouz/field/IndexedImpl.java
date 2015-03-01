package io.rouz.field;

import com.google.common.base.Preconditions;

import static io.rouz.field.HeaderField.IndexedName;

class IndexedImpl extends AbstractHeaderFieldImpl implements IndexedName {

  private final HeaderName name;

  IndexedImpl(HeaderName name) {
    super(HeaderFieldType.INDEXED);

    Preconditions.checkArgument(name.indexed(), "name is not indexed");
    this.name = name;
  }

  @Override
  public void visit(HeaderFieldVisitor visitor) {
    visitor.indexedName(this, true);
  }

  @Override
  public int index() {
    return name.index();
  }
}
