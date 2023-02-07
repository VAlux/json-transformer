package dev.alvo.utils;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Utils {

  public static <A, B> B getOrNull(A value, Function<A, B> extractor) {
    if (value != null) {
      return extractor.apply(value);
    }

    return null;
  }

  public static <A, B, C> C
  getOrNull(A value, Function<A, B> extractor1, Function<B, C> extractor2) {
    if (value != null) {
      return getOrNull(extractor1.apply(value), extractor2);
    }

    return null;
  }

  /**
   * Basic folding of the specified iterable source seeded with initial value and accumulated by the
   * provided operation
   */
  public static <T, R> R fold(final Iterable<T> source,
                              final R initial,
                              final BiFunction<R, T, R> operation) {
    var accumulator = initial;

    for (T element : source) {
      accumulator = operation.apply(accumulator, element);
    }

    return accumulator;
  }
}
