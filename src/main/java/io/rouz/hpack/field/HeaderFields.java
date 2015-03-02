package io.rouz.hpack.field;

import com.google.common.base.Preconditions;

import static io.rouz.hpack.field.HeaderFieldType.LITERAL_INDEX;
import static io.rouz.hpack.field.HeaderFieldType.LITERAL_NEVER_INDEX;
import static io.rouz.hpack.field.HeaderFieldType.LITERAL_NO_INDEX;

public final class HeaderFields {

  private HeaderFields() {}

  /**
   * Creates a {@link HeaderName} representing an indexed header name reference.
   *
   * @param index  The index into the static or dynamic table
   * @return A {@link HeaderName}
   */
  public static HeaderName name(final int index) {
    return new IndexedHeaderName(index);
  }

  /**
   * Creates a {@link HeaderName} representing a literal header name.
   *
   * @param name  The header name
   * @return A {@link HeaderName}
   */
  public static HeaderName name(final String name) {
    return new LiteralHeaderName(name);
  }

  /**
   * Creates a {@link HeaderField} representation of a fully indexed header field.
   *
   * @param index  The index into the static or dynamic table
   * @return A {@link HeaderField}
   */
  public static HeaderField indexedField(int index) {
    return new IndexedImpl(name(index));
  }

  /**
   * Creates a literal {@link HeaderField} representation that will be indexed in the dynamic table.
   *
   * @param name   A {@link HeaderName} instance
   * @param value  A {@link String} value
   * @return A {@link HeaderField}
   */
  public static HeaderField indexedField(HeaderName name, String value) {
    Preconditions.checkNotNull(name, "name");
    Preconditions.checkNotNull(value, "value");

    return createFromName(name, value, LITERAL_INDEX);
  }

  /**
   * Creates a literal {@link HeaderField} representation that will not alter the dynamic table.
   *
   * @param name   A {@link HeaderName} instance
   * @param value  A {@link String} value
   * @return A {@link HeaderField}
   */
  public static HeaderField nonIndexedField(HeaderName name, String value) {
    Preconditions.checkNotNull(name, "name");
    Preconditions.checkNotNull(value, "value");

    return createFromName(name, value, LITERAL_NO_INDEX);
  }

  /**
   * Creates a literal {@link HeaderField} representation that will not alter the dynamic table.
   *
   * The representation must also be treated as a never indexed field by itermediaries.
   *
   * @param name   A {@link HeaderName} instance
   * @param value  A {@link String} value
   * @return A {@link HeaderField}
   */
  public static HeaderField neverIndexedField(HeaderName name, String value) {
    Preconditions.checkNotNull(name, "name");
    Preconditions.checkNotNull(value, "value");

    return createFromName(name, value, LITERAL_NEVER_INDEX);
  }

  private static HeaderField createFromName(HeaderName name, String value, HeaderFieldType type) {
    return name.indexed()
        ? new LiteralIndexedImpl(name, value, type)
        : new LiteralLiteralImpl(name, value, type);
  }
}
