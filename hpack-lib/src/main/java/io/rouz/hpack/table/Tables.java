package io.rouz.hpack.table;

import io.rouz.hpack.field.HeaderField;
import io.rouz.hpack.field.HeaderFieldRepresentation;

/**
 * TODO: document.
 *
 *
 * indexes in index address space [1, s+k]
 *   s static table size
 *   k dynamic table size
 *
 * encoder usage
 *   name, value -> index
 *
 * decoder usage
 *   index -> name, value
 *
 *
 * tables keep up to date with header field representation stream
 */
public final class Tables {

  private Tables() {}

  public static DynamicTable dynamic(int capacity) {
    return new DynamicTable.Impl(capacity, -1);
  }

  interface EncoderTable {
    HeaderFieldRepresentation get(HeaderField headerField);
  }

  interface DecoderTable {
    HeaderField get(int index);
  }

}
