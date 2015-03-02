package io.rouz.hpack.field;

import java.util.Optional;

/**
 * TODO: document.
 */
public final class HeaderFieldCollectors {

  public static Optional<Integer> index(HeaderField headerField) {
    final CollectorVisitor collector = new CollectorVisitor();
    headerField.visit(collector);

    if (collector.indexedName != null) {
      return Optional.of(collector.indexedName.index());
    } else {
      return Optional.empty();
    }
  }

  public static Optional<String> name(HeaderField headerField) {
    final CollectorVisitor collector = new CollectorVisitor();
    headerField.visit(collector);

    if (collector.literalName != null) {
      return Optional.of(collector.literalName.name());
    } else {
      return Optional.empty();
    }
  }

  public static Optional<String> value(HeaderField headerField) {
    final CollectorVisitor collector = new CollectorVisitor();
    headerField.visit(collector);

    if (collector.literalValue != null) {
      return Optional.of(collector.literalValue.value());
    } else {
      return Optional.empty();
    }
  }

  private static class CollectorVisitor implements HeaderField.HeaderFieldVisitor {

    private HeaderField.IndexedName indexedName;
    private HeaderField.LiteralName literalName;
    private HeaderField.LiteralValue literalValue;

    @Override
    public void indexedName(HeaderField.IndexedName indexedName, boolean indexedField) {
      this.indexedName = indexedName;
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
