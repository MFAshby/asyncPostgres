package com.ashbysoft.asyncdb;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Asynchrous database API based on java nio and functional interfaces
 * Created by mfash on 30/08/2016.
 */
public interface Connection {
    void execQuery(String sqlQuery, BiConsumer<Throwable, CachedResultSet> resultsHandler);
    void close(Consumer<Throwable> closeHandler);
}
