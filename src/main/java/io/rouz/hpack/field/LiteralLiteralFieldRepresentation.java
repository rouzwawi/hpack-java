package io.rouz.hpack.field;

import com.google.common.base.Preconditions;

import static io.rouz.hpack.field.HeaderFieldRepresentation.LiteralName;
import static io.rouz.hpack.field.HeaderFieldRepresentation.LiteralValue;

final class LiteralLiteralFieldRepresentation extends AbstractHeaderFieldRepresentation
    implements LiteralName, LiteralValue {

  private final HeaderName name;
  private final String value;

  public LiteralLiteralFieldRepresentation(HeaderName name, String value, HeaderFieldType type) {
    super(type);

    Preconditions.checkArgument(!name.indexed(), "name is indexed");
    this.name = name;
    this.value = value;
  }

  @Override
  public <T extends HeaderFieldVisitor> T visit(T visitor) {
    visitor.literalName(this);
    visitor.literalValue(this);
    return visitor;
  }

  @Override
  public String name() {
    return name.literalName();
  }

  @Override
  public String value() {
    return value;
  }
}
