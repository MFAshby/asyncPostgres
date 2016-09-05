package com.ashbysoft.asyncdb;

import com.ashbysoft.asyncdb.postgres.PostgresDriver;
import com.ashbysoft.asyncdb.postgres.PostgresUtils;

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

                connection.execQuery("SELECT key, name, geom, image, grade\n" +
                        "  FROM public.routes LIMIT 1;", (throwable, resultSet) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else {
                        for (int i = 0; i < resultSet.getRowCount(); i++) {
                            String name = resultSet.getString(i, "name");
                            Integer key = resultSet.getInt(i, "key");
                            byte[] geom = resultSet.getByteArray(i, "geom");
                            System.out.println(String.format("name [%s] key [%d] geom[%s]", name, key, PostgresUtils.bytesToHexString(geom)));
                        }
                    }

                    connection.close(throwable1 -> {
                        System.out.println("COnnection closed, throwable [" + throwable1 + "]");
                        cdl.countDown();
                    });
                });
            });
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
