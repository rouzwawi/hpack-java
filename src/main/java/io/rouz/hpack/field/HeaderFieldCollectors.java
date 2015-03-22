package io.rouz.hpack.field;

import java.util.Optional;

/**
 * TODO: document.
 */
public final class HeaderFieldCollectors {

  private HeaderFieldCollectors() {}

  public static Optional<Integer> index(HeaderFieldRepresentation headerFieldRepresentation) {
    final CollectorVisitor collector = new CollectorVisitor();
    headerFieldRepresentation.visit(collector);

    if (collector.indexedName != null) {
      return Optional.of(collector.indexedName.index());
    } else {
      return Optional.empty();
    }
  }

  public static Optional<String> name(HeaderFieldRepresentation headerFieldRepresentation) {
    final CollectorVisitor collector = new CollectorVisitor();
    headerFieldRepresentation.visit(collector);

    if (collector.literalName != null) {
      return Optional.of(collector.literalName.name());
    } else {
      return Optional.empty();
    }
  }

  public static Optional<String> value(HeaderFieldRepresentation headerFieldRepresentation) {
    final CollectorVisitor collector = new CollectorVisitor();
    headerFieldRepresentation.visit(collector);

    if (collector.literalValue != null) {
      return Optional.of(collector.literalValue.value());
    } else {
      return Optional.empty();
    }
  }

  private static class CollectorVisitor implements HeaderFieldRepresentation.HeaderFieldVisitor {

    private HeaderFieldRepresentation.IndexedName indexedName;
    private HeaderFieldRepresentation.LiteralName literalName;
    private HeaderFieldRepresentation.LiteralValue literalValue;


    @Override
    public void indexedName(HeaderFieldRepresentation.IndexedName indexedName, boolean indexedField) {
      this.indexedName = indexedName;
    }

    @Override
    public void literalName(HeaderFieldRepresentation.LiteralName literalName) {
      this.literalName = literalName;
    }

    @Override
    public void literalValue(HeaderFieldRepresentation.LiteralValue literalValue) {
      this.literalValue = literalValue;
    }
  }

}
