package com.ashbysoft.asyncdb;

import com.ashbysoft.asyncdb.postgres.PostgresDriver;

import java.util.concurrent.CountDownLatch;

public class TestMain {
    public static void main(String[] args) {
        try {
            CountDownLatch cdl = new CountDownLatch(1);
            new PostgresDriver().connect("localhost", 5432, "climbtracker", "climbserver", "12345", (connection, ex) -> {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                System.out.println(String.valueOf(connection));

                connection.execQuery("SELECT * FROM routes LIMIT 1;", (throwable, resultSet) -> {
                    System.out.println("Query completed");
                    cdl.countDown();
                });
            });
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
