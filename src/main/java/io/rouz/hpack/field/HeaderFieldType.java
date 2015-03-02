package io.rouz.hpack.field;

/**
 * Representations of the MSB of the four different Header Field Representations.
 */
public enum HeaderFieldType {
  INDEXED(0x80, 1),
  LITERAL_INDEX(0x40, 2),
  LITERAL_NO_INDEX(0x00, 4),
  LITERAL_NEVER_INDEX(0x10, 4);

  private final int pattern;
  private final int bits;

  HeaderFieldType(int pattern, int bits) {
    this.pattern = pattern;
    this.bits = bits;
  }

  /**
   * The bit pattern to be used for this field type. Starting at bit 8 and down.
   *
   * @return An integer containing the bit pattern in the lowest octet.
   */
  public int pattern() {
    return pattern;
  }

  /**
   * The number of bits used from {@link #pattern()}.
   *
   * @return The number of bits used
   */
  public int bits() {
    return bits;
  }
}
