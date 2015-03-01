package io.rouz.field;

import com.google.common.base.Preconditions;

import static io.rouz.field.HeaderField.LiteralName;
import static io.rouz.field.HeaderField.LiteralValue;

class LiteralLiteralImpl extends AbstractHeaderFieldImpl implements LiteralName, LiteralValue {

  private final HeaderName name;
  private final String value;

  public LiteralLiteralImpl(HeaderName name, String value, HeaderFieldType type) {
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
