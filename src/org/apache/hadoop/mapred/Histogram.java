package org.apache.hadoop.mapred;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Histogram {

  private HashMap<Integer, AtomicInteger> counts;
  private AtomicInteger total;
  private String name;
  private static final long seed = 1999;
  private static final double PROBABILITY_RESOLUTION = 1e-4;

  public Histogram() {
    this.counts = new HashMap<Integer, AtomicInteger>();
    total = new AtomicInteger(0);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addValue(int val) {
    AtomicInteger oldCount = counts.get(val);
    if (oldCount != null) {
      oldCount.incrementAndGet();
    } else {
      counts.put(val, new AtomicInteger(1));
    }
    total.incrementAndGet();
  }

  public double probability(int x) {
    if (total.get() == 0) {
      return 0;
    }
    AtomicInteger count = counts.get(x);
    return (count == null) ? 1.0 / total.get() : (double) count.get() / total.get();
  }

  public double cumulativeProbability(int x) {
    if (total.get() == 0) {
      return 1.0;
    }
    int countsLessThanX = 0;
    for (Map.Entry<Integer, AtomicInteger> e : counts.entrySet()) {
      if (e.getKey() <= x) {
        countsLessThanX += e.getValue().get();
      }
    }
    return (double) countsLessThanX / total.get();
  }

  public double percentBelowMe(double x) {
    return cumulativeProbability((int) x) * 100.0;
  }

  public int getN() {
    return total.get();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(":>[");
    for (Map.Entry<Integer, AtomicInteger> e : counts.entrySet()) {
      builder.append(e.getKey());
      builder.append(':');
      builder.append(e.getValue().get());
      builder.append(", ");
    }
    builder.append(']');
    return builder.toString();
  }

  // Test
  public static void main(String[] args) {
    Histogram h = new Histogram();
    for (int i = 0; i < 10; i++) {
      h.addValue(1);
      h.addValue(2);
    }
    System.out.println("Probab. of 1 should be 0.5 : " + h.probability(1));
    System.out.println("Probab. of 2 should be 0.5 : " + h.probability(2));
  }
}  
