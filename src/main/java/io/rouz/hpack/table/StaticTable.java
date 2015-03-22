package io.rouz.hpack.table;

import io.rouz.hpack.field.HeaderField;

/**
 * TODO: document.
 */
public final class StaticTable {

  private StaticTable() {}

  private static final HeaderField[] TABLE = new HeaderField[] {
      HeaderField.create(":authority"),
      HeaderField.create(":method", "GET"),
      HeaderField.create(":method", "POST"),
      HeaderField.create(":path", "/"),
      HeaderField.create(":path", "/index.html"),
      HeaderField.create(":scheme", "http"),
      HeaderField.create(":scheme", "https"),
      HeaderField.create(":status", "200"),
      HeaderField.create(":status", "204"),
      HeaderField.create(":status", "206"),
      HeaderField.create(":status", "304"),
      HeaderField.create(":status", "400"),
      HeaderField.create(":status", "404"),
      HeaderField.create(":status", "500"),
      HeaderField.create("accept-charset"),
      HeaderField.create("accept-encoding", "gzip, deflate"),
      HeaderField.create("accept-language"),
      HeaderField.create("accept-ranges"),
      HeaderField.create("accept"),
      HeaderField.create("access-control-allow-origin"),
      HeaderField.create("age"),
      HeaderField.create("allow"),
      HeaderField.create("authorization"),
      HeaderField.create("cache-control"),
      HeaderField.create("content-disposition"),
      HeaderField.create("content-encoding"),
      HeaderField.create("content-language"),
      HeaderField.create("content-length"),
      HeaderField.create("content-location"),
      HeaderField.create("content-range"),
      HeaderField.create("content-type"),
      HeaderField.create("cookie"),
      HeaderField.create("date"),
      HeaderField.create("etag"),
      HeaderField.create("expect"),
      HeaderField.create("expires"),
      HeaderField.create("from"),
      HeaderField.create("host"),
      HeaderField.create("if-match"),
      HeaderField.create("if-modified-since"),
      HeaderField.create("if-none-match"),
      HeaderField.create("if-range"),
      HeaderField.create("if-unmodified-since"),
      HeaderField.create("last-modified"),
      HeaderField.create("link"),
      HeaderField.create("location"),
      HeaderField.create("max-forwards"),
      HeaderField.create("proxy-authenticate"),
      HeaderField.create("proxy-authorization"),
      HeaderField.create("range"),
      HeaderField.create("referer"),
      HeaderField.create("refresh"),
      HeaderField.create("retry-after"),
      HeaderField.create("server"),
      HeaderField.create("set-cookie"),
      HeaderField.create("strict-transport-security"),
      HeaderField.create("transfer-encoding"),
      HeaderField.create("user-agent"),
      HeaderField.create("vary"),
      HeaderField.create("via"),
      HeaderField.create("www-authenticate")
  };

  public static int size() {
    return TABLE.length;
  }

  public static HeaderField get(int i) {
    if (i <= 0 || size() < i) {
      throw new IndexOutOfBoundsException();
    }

    // static table entries are 1-indexed
    return TABLE[i - 1];
  }

}
