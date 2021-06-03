package ru.nsu.fit.g18214.dzaikov;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public final class Founder {
  private final CyclicBarrier barrier;
  private final List<Runnable> workers;
  private final Company company;
  public Founder(final Company company) {
    this.company = company;
    this.workers = new ArrayList<>(company.getDepartmentsCount());
    this.barrier = new CyclicBarrier(company.getDepartmentsCount() + 1);
    for (int i = 0; i < company.getDepartmentsCount(); i++) {
      final int I = i;
      this.workers.add(i, ()->{
        company.getFreeDepartment(I).performCalculations();
        try {
          barrier.await();
        } catch (InterruptedException e) {
          e.printStackTrace();
        } catch (BrokenBarrierException ignored) {

        }
      });
    }
  }
  public void start() {
    for (final Runnable worker : workers) {
      new Thread(worker).start();
    }
    try {
      barrier.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (BrokenBarrierException ignored) {

    }
    company.showCollaborativeResult();
  }
}
