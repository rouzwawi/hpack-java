package io.rouz.hpack.field;

/**
 * TODO: document.
 */
public interface HeaderName {
  boolean indexed();
  int index();
  String literalName();
}
