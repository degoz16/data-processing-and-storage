package ru.nsu.fit.dzaikov.t10;

public class App {
  public static void main(String[] args) {
    Object mutex = new Object();
    boolean flag = true;
    Thread child =
        new Thread(
            () -> {
              for (int i = 0; i < 10; i++) {
                synchronized (mutex) {
                  try {
                    mutex.wait();
                  } catch (InterruptedException ignored) {

                  }
                }
                System.out.println("Child.");
                synchronized (mutex) {
                  mutex.notify();
                }
              }
            },
            "Child");
    child.start();
    for (int i = 0; i < 10; i++) {
      if (flag) {
        flag = false;
      } else {
        synchronized (mutex) {
          try {
            mutex.wait();
          } catch (InterruptedException ignored) {

          }
        }
      }
      System.out.println("Parent.");
      synchronized (mutex) {
        mutex.notify();
      }
    }
  }
}
