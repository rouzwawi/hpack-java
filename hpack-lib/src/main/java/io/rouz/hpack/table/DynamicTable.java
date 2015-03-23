package io.rouz.hpack.table;

import io.rouz.hpack.field.HeaderField;

/**
 * TODO: document.
 */
public interface DynamicTable {

  /**
   * 1-indexed, constant time table access.
   *
   * @param i  The index to get
   * @return  The {@link HeaderField} at index {@code i} in the table
   */
  HeaderField get(int i);

  void put(String name, String value);

  int size();
  int maxSize();

  // TODO: size calculations and eviction
  static class Impl implements DynamicTable {

    // we'll aim to not overrun the table;
    private final CircularArrayList<HeaderField> table;
    private final int maxSize;

    Impl(int capacity, int maxSize) {
      this.table = new CircularArrayList<>(capacity);
      this.maxSize = maxSize;
    }

    @Override
    public HeaderField get(int i) {
      return table.get(table.size() - i);
    }

    @Override
    public void put(String name, String value) {
      table.add(HeaderField.create(name, value));
    }

    @Override
    public int size() {
      throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int maxSize() {
      throw new UnsupportedOperationException("not implemented");
    }
  }
}
