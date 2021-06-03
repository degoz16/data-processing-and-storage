package ru.nsu.xml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class XmlToDataBase {
  private static final int THREAD_CNT = 4;
  private static final String addPersonTable =
      "CREATE TABLE IF NOT EXISTS persons_table("
          + "id varchar(8) NOT NULL PRIMARY KEY,"
          + "gender varchar(2) NOT NULL,"
          + "firstname varchar(255) NOT NULL,"
          + "lastname varchar(255) NOT NULL,"
          + "CONSTRAINT gender_check CHECK (gender IN ('M', 'F'))"
          + ")";
  private static final String addRelTable =
      "CREATE TABLE IF NOT EXISTS relations_table("
          + "first_id varchar(8) NOT NULL,"
          + "relation varchar(16) NOT NULL,"
          + "second_id varchar(8) NOT NULL,"
          + "CONSTRAINT relation_check CHECK (relation "
          + "IN ('MOTHER', 'FATHER', 'SISTER', 'BROTHER',"
          + " 'SON', 'DAUGHTER', 'SPOUSE'))"
          + ")";
  private static final String insertPerson = "INSERT INTO persons_table VALUES (?, ?, ?, ?)";
  private static final String insertRelation = "INSERT INTO relations_table VALUES (?, ?, ?)";

  private static final String alterRelTable =
      "ALTER TABLE relations_table "
          + "ADD FOREIGN KEY (first_id) REFERENCES persons_table(id), "
          + "ADD FOREIGN KEY (second_id) REFERENCES persons_table(id)";

  public static void doWork(String path) {
    System.out.println("File split...");
    List<File> xmlFiles = new ArrayList<>();
    File xml;
    Path tmpDir = null;
    try {
      tmpDir = Files.createTempDirectory("TEMP");
      xml = new File(path);
      int linesCnt = 0;
      try (BufferedReader br = new BufferedReader(new FileReader(xml))) {
        while (br.readLine() != null) {
          linesCnt++;
        }
      }
      int chunkSize = linesCnt / THREAD_CNT;
      try (BufferedReader br = new BufferedReader(new FileReader(xml))) {
        for (int i = 0; i < THREAD_CNT; i++) {
          Path tmpFile = Files.createTempFile(tmpDir, "tmp" + i + ".xml", null);
          try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile.toFile()))) {
            if (i > 0) {
              bw.write(
                  "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                      + "<ns2:persons xmlns:ns2=\"http://xml1/persons\">\n");
              bw.flush();
            }
            String s = null;
            for (int j = 0; j < chunkSize; j++) {
              s = br.readLine();
              if (s == null) {
                break;
              }
              bw.write(s + "\n");
              bw.flush();
            }
            if (s == null) {
              if (i < THREAD_CNT - 1) {
                bw.write("</ns2:persons>");
                bw.flush();
              }
              xmlFiles.add(tmpFile.toFile());
              break;
            }
            if (!s.contains("</person>")) {
              s = br.readLine();
              while (s != null) {
                bw.write(s + "\n");
                bw.flush();
                if (s.contains("</person>")) {
                  break;
                }
                s = br.readLine();
              }
            }
            if (i == THREAD_CNT - 1 && s != null) {
              while (s != null) {
                bw.write(s + "\n");
                bw.flush();
                s = br.readLine();
              }
            }
            if (i < THREAD_CNT - 1) {
              bw.write("</ns2:persons>");
              bw.flush();
            }
          }
          xmlFiles.add(tmpFile.toFile());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    try (Connection connection =
        DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/persons", "postgres", "postgres")) {
      System.out.println("DB creation...");
      Statement statement = connection.createStatement();
      statement.executeUpdate("DROP TABLE IF EXISTS relations_table  ");
      statement.executeUpdate("DROP TABLE IF EXISTS persons_table ");

      statement.executeUpdate(addPersonTable);
      statement.executeUpdate(addRelTable);

      List<Thread> workers = new ArrayList<>();
      for (int i = 0; i < THREAD_CNT; i++) {
        workers.add(
            new Thread(
                new PersonProcessor(
                    connection.prepareStatement(insertPerson),
                    connection.prepareStatement(insertRelation),
                    xmlFiles.get(i))));
      }
      System.out.println("Pushing persons...");
      workers.forEach(Thread::start);
      try {
        for (Thread worker : workers) {
          worker.join();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      statement.executeUpdate(alterRelTable);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    xmlFiles.forEach(File::deleteOnExit);
    if (tmpDir != null) {
      tmpDir.toFile().deleteOnExit();
    }
  }
}
