/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ru.nsu.fit.g18214.dzaikov;

public class App {
    public static void main(String[] args) {
        Company company = new Company(10);
        Founder founder = new Founder(company);
        founder.start();
    }
}