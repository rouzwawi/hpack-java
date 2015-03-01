package io.rouz.field;

/**
 * An interface for instances of the binary format Header Fields Representation as described by
 * the HPACK RFC.
 *
 * A {@link HeaderField} can be any of the four indexed/literal types. This can be examined by
 * the {@link #type()} accessor.
 *
 * All other information exchange should happen through the utility {@link HeaderFieldVisitor}s.
 *
 * TODO: implement visitors
 */
public interface HeaderField {

  HeaderFieldType type();

  void visit(HeaderFieldVisitor visitor);

  interface IndexedName extends HeaderField {
    int index();
  }

  interface LiteralName extends HeaderField {
    String name();
  }

  interface LiteralValue extends HeaderField {
    String value();
  }

  interface HeaderFieldVisitor {
    void indexedName(IndexedName indexedName, boolean indexedField);
    void literalName(LiteralName literalName);
    void literalValue(LiteralValue literalValue);
  }
}