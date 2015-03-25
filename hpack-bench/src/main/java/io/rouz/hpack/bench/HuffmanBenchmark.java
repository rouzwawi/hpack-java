package io.rouz.hpack.bench;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.ByteBuffer;

import io.rouz.hpack.primitive.HuffmanCodec;
import io.rouz.hpack.primitive.HuffmanDecodingError;

/**
 * Result: 1357439.116 ±(99.9%) 13316.684 ops/s [Average]
 * Statistics: (min, avg, max) = (1166769.883, 1357439.116, 1411256.679), stdev = 39264.546
 * Confidence interval (99.9%): [1344122.432, 1370755.800]
 *
 * # Run complete. Total time: 00:04:13
 *
 * Benchmark                           Mode  Cnt        Score       Error  Units
 * HuffmanBenchmark.lookupTrieDecode  thrpt  100  3465599.330 ± 18934.934  ops/s
 * HuffmanBenchmark.naiveTrieDecode   thrpt  100  1357439.116 ± 13316.684  ops/s
 */
@State(Scope.Thread)
public class HuffmanBenchmark {

  static final byte[] BYTES = new byte[] {
      (byte) 0xad, (byte) 0x94, (byte) 0xe7, (byte) 0x82, (byte) 0x1d, (byte) 0xd7, (byte) 0xf2,
      (byte) 0xe6, (byte) 0xc7, (byte) 0xb3, (byte) 0x35, (byte) 0xdf, (byte) 0xdf, (byte) 0xcd,
      (byte) 0x5b, (byte) 0x39, (byte) 0x60, (byte) 0xd5, (byte) 0xaf, (byte) 0x27, (byte) 0x08,
      (byte) 0x7f, (byte) 0x36, (byte) 0x72, (byte) 0xc1, (byte) 0xab, (byte) 0x27, (byte) 0x0f,
      (byte) 0xb5, (byte) 0x29, (byte) 0x1f, (byte) 0x95, (byte) 0x87, (byte) 0x31, (byte) 0x60,
      (byte) 0x65, (byte) 0xc0, (byte) 0x03, (byte) 0xed, (byte) 0x4e, (byte) 0xe5, (byte) 0xb1,
      (byte) 0x06, (byte) 0x3d, (byte) 0x50, (byte) 0x07
  };

  static final HuffmanCodec NTRIE = HuffmanCodec.NAIVE_TRIE_INSTANCE;
  static final HuffmanCodec LTRIE = HuffmanCodec.LOOKUP_TRIE_INSTANCE;

  @Benchmark
  public byte[] naiveTrieDecode() throws HuffmanDecodingError {
    return NTRIE.decode(ByteBuffer.wrap(BYTES));
  }

  @Benchmark
  public byte[] lookupTrieDecode() throws HuffmanDecodingError {
    return LTRIE.decode(ByteBuffer.wrap(BYTES));
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(".*" + HuffmanBenchmark.class.getSimpleName() + ".*")
        .warmupIterations(5)
        .measurementIterations(20)
        .forks(5)
        .build();

    new Runner(opt).run();
  }
}
