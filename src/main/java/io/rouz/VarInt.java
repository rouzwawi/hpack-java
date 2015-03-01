package io.rouz;

/**
 * TODO: document.
 */
public interface VarInt {

  public static final VarInt INSTANCE = new VarIntImpl();

  int decode(int n, byte[] number);

  int decode(int n, byte[] number, int p);

  int encode(int x, int n, byte[] number);

  int encode(int x, int n, byte[] number, int p);
}
