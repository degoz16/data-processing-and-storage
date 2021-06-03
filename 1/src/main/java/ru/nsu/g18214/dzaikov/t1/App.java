package ru.nsu.g18214.dzaikov.t1;

public class App {
  public static void main(String[] args) {
    Thread child = new Thread(() -> {
      for (int i = 0; i < 10; i++) {
        System.out.println("Child.");
      }
    }, "Child");
    child.start();
    for (int i = 0; i < 10; i++) {
      System.out.println("Parent.");
    }
  }
}
