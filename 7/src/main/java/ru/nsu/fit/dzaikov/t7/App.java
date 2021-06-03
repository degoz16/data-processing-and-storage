/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ru.nsu.fit.dzaikov.t7;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class App {
  private static final int N = 100000000;

  public static void main(String[] args) {
    final int threadsCnt = Integer.parseInt(args[0]);
    final CyclicBarrier barrier = new CyclicBarrier(threadsCnt + 1);
    Thread[] threads = new Thread[threadsCnt];
    PiCalculator[] piCalculators = new PiCalculator[threadsCnt];

    for (int i = 0; i < threadsCnt; i++) {
      final int I = i;
      piCalculators[i] = new PiCalculator(barrier, i * N / threadsCnt, (i + 1) * N / threadsCnt);
      threads[i] = new Thread(piCalculators[i]);
      threads[i].start();
    }
    try {
      barrier.await();
    } catch (InterruptedException | BrokenBarrierException ignored) {

    }
    double res = 0;
    for (int i = 0; i < threadsCnt; i++) {
      res += piCalculators[i].getSum();
    }
    res *= 4;
    System.out.println(res);
  }
}