package io.rouz.hpack.field;

import com.google.common.base.Preconditions;

import static io.rouz.hpack.field.HeaderField.LiteralName;
import static io.rouz.hpack.field.HeaderField.LiteralValue;

final class LiteralLiteralField extends AbstractHeaderField
    implements LiteralName, LiteralValue {

  private final HeaderName name;
  private final String value;

  public LiteralLiteralField(HeaderName name, String value, HeaderFieldType type) {
    super(type);

    Preconditions.checkArgument(!name.indexed(), "name is indexed");
    this.name = name;
    this.value = value;
  }

  @Override
  public void visit(HeaderFieldVisitor visitor) {
    visitor.literalName(this);
    visitor.literalValue(this);
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
