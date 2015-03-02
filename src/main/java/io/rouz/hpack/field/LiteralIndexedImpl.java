package io.rouz.hpack.field;

import com.google.common.base.Preconditions;

import static io.rouz.hpack.field.HeaderField.IndexedName;
import static io.rouz.hpack.field.HeaderField.LiteralValue;

class LiteralIndexedImpl extends AbstractHeaderFieldImpl implements IndexedName, LiteralValue {

  private final HeaderName name;
  private final String value;

  public LiteralIndexedImpl(HeaderName name, String value, HeaderFieldType type) {
    super(type);

    Preconditions.checkArgument(name.indexed(), "name is not indexed");
    this.name = name;
    this.value = value;
  }

  @Override
  public void visit(HeaderFieldVisitor visitor) {
    visitor.indexedName(this, false);
    visitor.literalValue(this);
  }

  @Override
  public int index() {
    return name.index();
  }

  @Override
  public String value() {
    return value;
  }
}
