package edu.wpi.cs3733.c22.teamD.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ArrayUtil {

  public static <T> Consumer<T> lambdaCounter(BiConsumer<Integer, T> consumer) {
    AtomicInteger counter = new AtomicInteger(0);
    return item -> consumer.accept(counter.getAndIncrement(), item);
  }
}
