package io.rouz.hpack.field;

import com.google.common.base.Preconditions;

import static io.rouz.hpack.field.HeaderFieldRepresentation.IndexedName;
import static io.rouz.hpack.field.HeaderFieldRepresentation.LiteralValue;

final class LiteralIndexedFieldRepresentation extends AbstractHeaderFieldRepresentation
    implements IndexedName, LiteralValue {

  private final HeaderName name;
  private final String value;

  public LiteralIndexedFieldRepresentation(HeaderName name, String value, HeaderFieldType type) {
    super(type);

    Preconditions.checkArgument(name.indexed(), "name is not indexed");
    this.name = name;
    this.value = value;
  }

  @Override
  public <T extends HeaderFieldVisitor> T visit(T visitor) {
    visitor.indexedName(this, false);
    visitor.literalValue(this);
    return visitor;
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
