package io.rouz;

import com.google.common.base.Preconditions;

public class HeaderFields {

  interface HeaderField {
    HeaderFieldType type();

    void visit(HeaderFieldVisitor visitor);
  }

  interface IndexedName extends HeaderField {
    int index();
  }

  interface LiteralName extends HeaderField {
    String name();
  }

  interface LiteralValue extends HeaderField {
    String value();
  }

  interface HeaderName {
    boolean indexed();

    int index();
    String literalName();
  }

  interface HeaderFieldVisitor {
    void indexedName(IndexedName indexedName, boolean indexedField);
    void literalName(LiteralName literalName);
    void literalValue(LiteralValue literalValue);
  }

  public static HeaderName name(final int index) {
    return new HeaderName() {
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
    };
  }

  public static HeaderName name(final String name) {
    return new HeaderName() {
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
    };
  }

  public static HeaderField createIndexed(int index) {
    return createIndexed(name(index), null);
  }

  public static HeaderField createIndexed(HeaderName name, String value) {
    Preconditions.checkNotNull(name, "name");

    if (value != null) {
      return createFromName(name, value, HeaderFieldType.LITERAL_INDEX);
    } else {
      return new IndexedImpl(name);
    }
  }

  public static HeaderField createNonIndexed(HeaderName name, String value) {
    return createNonIndexed(name, value, false);
  }

  public static HeaderField createNonIndexed(HeaderName name, String value, boolean never) {
    Preconditions.checkNotNull(name, "name");
    Preconditions.checkNotNull(value, "value");

    final HeaderFieldType type = never
        ? HeaderFieldType.LITERAL_NEVER_INDEX
        : HeaderFieldType.LITERAL_NO_INDEX;

    return createFromName(name, value, type);
  }

  private static HeaderField createFromName(HeaderName name, String value, HeaderFieldType type) {
    return name.indexed()
        ? new LiteralIndexedImpl(name, value, type)
        : new LiteralLiteralImpl(name, value, type);
  }

  static abstract class AbstractHeaderFieldImpl implements HeaderField {

    private final HeaderFieldType type;

    protected AbstractHeaderFieldImpl(HeaderFieldType type) {
      this.type = type;
    }

    @Override
    public HeaderFieldType type() {
      return type;
    }
  }

  static class IndexedImpl extends AbstractHeaderFieldImpl
      implements IndexedName {

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

  static class LiteralIndexedImpl extends AbstractHeaderFieldImpl
      implements IndexedName, LiteralValue {

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

  static class LiteralLiteralImpl extends AbstractHeaderFieldImpl
      implements LiteralName, LiteralValue {

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

  enum HeaderFieldType {
    INDEXED(0x80, 7),
    LITERAL_INDEX(0x40, 6),
    LITERAL_NO_INDEX(0x00, 4),
    LITERAL_NEVER_INDEX(0x10, 4);

    private final int bits;
    private final int indexPrefix;

    HeaderFieldType(int bits, int indexPrefix) {
      this.bits = bits;
      this.indexPrefix = indexPrefix;
    }

    public int bits() {
      return bits;
    }

    public int indexPrefix() {
      return indexPrefix;
    }
  }
}
