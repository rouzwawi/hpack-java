package io.rouz.hpack.field;

import java.util.Optional;

/**
 * TODO: document.
 */
public final class HeaderFieldCollectors {

  public static Optional<Integer> index(HeaderField field) {
    final CollectorVisitor collector = new CollectorVisitor();
    field.visit(collector);

    if (collector.indexedName != null) {
      return Optional.of(collector.indexedName.index());
    } else {
      return Optional.empty();
    }
  }

  private static class CollectorVisitor implements HeaderField.HeaderFieldVisitor {

    private boolean indexedField;
    private HeaderField.IndexedName indexedName;
    private HeaderField.LiteralName literalName;
    private HeaderField.LiteralValue literalValue;

    @Override
    public void indexedName(HeaderField.IndexedName indexedName, boolean indexedField) {
      this.indexedName = indexedName;
      this.indexedField = indexedField;
    }

    @Override
    public void literalName(HeaderField.LiteralName literalName) {
      this.literalName = literalName;
    }

    @Override
    public void literalValue(HeaderField.LiteralValue literalValue) {
      this.literalValue = literalValue;
    }
  }

}
