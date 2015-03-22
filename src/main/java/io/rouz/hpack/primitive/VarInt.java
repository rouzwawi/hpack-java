package io.rouz.hpack.primitive;

import java.nio.ByteBuffer;

/**
 * TODO: document.
 */
public interface VarInt {

  public static final VarInt INSTANCE = new VarIntImpl();

  int decode(int n, ByteBuffer buffer);

  int encode(int x, int n, ByteBuffer buffer);
}
