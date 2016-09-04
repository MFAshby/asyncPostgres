package com.ashbysoft.asyncdb;

import java.util.function.BiConsumer;

public interface Driver {
    void connect(String host, int port, String database, String username, String password,
                 BiConsumer<Connection, Throwable> connectedHandler);
}
