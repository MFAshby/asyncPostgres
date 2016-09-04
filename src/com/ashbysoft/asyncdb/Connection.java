package com.ashbysoft.asyncdb;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Asynchrous database API based on java nio and functional interfaces
 * Created by mfash on 30/08/2016.
 */
public interface Connection {
    void execQuery(String sqlQuery, BiConsumer<Throwable, ResultSet> resultsHandler);
}
