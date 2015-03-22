package io.rouz.hpack.field;

/**
 * An interface for instances of the binary format Header Fields Representation as described by
 * the HPACK RFC.
 *
 * A {@link HeaderFieldRepresentation} can be any of the four indexed/literal types. This can be examined by
 * the {@link #type()} accessor.
 *
 * All other information exchange should happen through the utility {@link HeaderFieldVisitor}s.
 */
public interface HeaderFieldRepresentation {

  HeaderFieldType type();

  <T extends HeaderFieldVisitor> T visit(T visitor);

  interface IndexedName extends HeaderFieldRepresentation {
    int index();
  }

  interface LiteralName extends HeaderFieldRepresentation {
    String name();
  }

  interface LiteralValue extends HeaderFieldRepresentation {
    String value();
  }

  interface HeaderFieldVisitor {
    void indexedName(IndexedName indexedName, boolean indexedField);
    void literalName(LiteralName literalName);
    void literalValue(LiteralValue literalValue);
  }
}
