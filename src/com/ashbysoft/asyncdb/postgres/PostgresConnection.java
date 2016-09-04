package com.ashbysoft.asyncdb.postgres;

import com.ashbysoft.asyncdb.Connection;
import com.ashbysoft.asyncdb.ResultSet;
import com.ashbysoft.asyncdb.postgres.messages.Query;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class PostgresConnection implements Connection {
    private AsynchronousSocketChannel sc = null;
    private Map<String, String> parameterMap = new HashMap<>();
    private int processID = -1;
    private int secretKey = -1;

    public PostgresConnection(AsynchronousSocketChannel sc) {
        this.sc = sc;
    }

    @Override
    public void execQuery(String sqlQuery, BiConsumer<Throwable, ResultSet> resultsHandler) {
        new Query(sqlQuery).send(sc, (throwable, postgresMessage) -> {
            if (throwable != null) {
                resultsHandler.accept(throwable, null);
                return;
            }

            // Row description, datarow datarow datarow.
        });
    }

//    CommandComplete
//    An SQL command completed normally.
//
//            CopyInResponse
//    The backend is ready to copy data from the frontend to a table; see Section 50.2.5.
//
//    CopyOutResponse
//    The backend is ready to copy data from a table to the frontend; see Section 50.2.5.
//
//    RowDescription
//    Indicates that rows are about to be returned in response to a SELECT, FETCH, etc query. The contents of this message describe the column layout of the rows. This will be followed by a DataRow message for each row being returned to the frontend.
//
//            DataRow
//    One of the set of rows returned by a SELECT, FETCH, etc query.
//
//            EmptyQueryResponse
//    An empty query string was recognized.
//
//    ErrorResponse
//    An error has occurred.
//
//    ReadyForQuery
//    Processing of the query string is complete. A separate message is sent to indicate this because the query string might contain multiple SQL commands. (CommandComplete marks the end of processing one SQL command, not the whole string.) ReadyForQuery will always be sent, whether processing terminates successfully or with an error.
//
//    NoticeResponse
//    A warning message has been issued in relation to the query. Notices are in addition to other responses, i.e., the backend will continue processing the command.

    public void addParameter(String parameterName, String parameterValue) {
        parameterMap.put(parameterName, parameterValue);
    }

    public void setBackendKeyData(int processID, int secretKey) {
        this.processID = processID;
        this.secretKey = secretKey;
    }

    public AsynchronousSocketChannel getSc() {
        return sc;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Connection:\n");
        parameterMap.keySet().forEach(s -> {
            sb.append(String.format("Key[%s] Value[%s]\n", s, parameterMap.get(s)));
        });
        sb.append(String.format("Process id [%d]\n", processID));
        sb.append(String.format("Secret Key [%d]\n", secretKey));
        return sb.toString();
    }
}
