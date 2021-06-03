package ru.nsu.fit.dzaikov.t7;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PiCalculator implements Runnable {
  private final CyclicBarrier barrier;
  private double sum = 0;
  private final int from;
  private final int to;

  public PiCalculator(CyclicBarrier barrier, int from, int to) {
    this.barrier = barrier;
    this.from = from;
    this.to = to;
  }

  private static double calc(int k) {
    return 1d / (double) (2 * k + 1) * (k % 2 == 0 ? 1 : -1);
  }

  @Override
  public void run() {
    for (int i = from; i < to; i++) {
      sum += calc(i);
    }
    try {
      barrier.await();
    } catch (BrokenBarrierException | InterruptedException ignored) {

    }
  }

  public double getSum() {
    return sum;
  }
}
