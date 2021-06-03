package ru.nsu.fit.dzaikov.t8;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PiCalculator implements Runnable {
  private boolean stopFlag = false;
  private final CyclicBarrier barrier;
  private double sum = 0;
  private int from;
  private int to;
  private final int step;

  public PiCalculator(CyclicBarrier barrier, int from, int to, int step) {
    this.barrier = barrier;
    this.from = from;
    this.to = to;
    this.step = step;
  }

  private static double calc(int k) {
    return 1d / (double) (2 * k + 1) * (k % 2 == 0 ? 1 : -1);
  }

  public void setStopFlag() {
    stopFlag = true;
  }

  @Override
  public void run() {
    while (!stopFlag) {
      for (int i = from; i < to; i++) {
        sum += calc(i);
      }
      try {
        barrier.await();
      } catch (BrokenBarrierException | InterruptedException ignored) {

      }
      from += step;
      to += step;
    }
  }

  public double getSum() {
    return sum;
  }
}
