package com.ashbysoft.asyncdb.postgres;

import com.ashbysoft.asyncdb.Connection;
import com.ashbysoft.asyncdb.CachedResultSet;
import com.ashbysoft.asyncdb.postgres.messages.*;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PostgresConnection implements Connection {
    private static final String CLIENT_ENCODING_KEY = "client_encoding";

    private AsynchronousSocketChannel sc = null;
    private Map<String, String> parameterMap = new HashMap<>();
    private int processID = -1;
    private int secretKey = -1;

    public PostgresConnection(AsynchronousSocketChannel sc) {
        this.sc = sc;
    }

    @Override
    public void execQuery(String sqlQuery, BiConsumer<Throwable, CachedResultSet> resultsHandler) {
        new Query(sqlQuery).send(sc, (throwable, postgresMessage) -> {
            if (throwable != null) {
                resultsHandler.accept(throwable, null);
                return;
            }

            createResultSetHandleResponse(postgresMessage, resultsHandler);
        });
    }

    @Override
    public void close(Consumer<Throwable> closeHandler) {
        new Terminate().send(sc, (throwable, postgresMessage) -> {
            try {
                sc.close();
                closeHandler.accept(null);
            } catch (IOException e) {
                closeHandler.accept(e);
            }
        });
    }

    private void createResultSetHandleResponse(PostgresMessage postgresMessage, BiConsumer<Throwable, CachedResultSet> resultsHandler) {

        PostgresCachedResultSet resultSet = new PostgresCachedResultSet(this);
        handleQueryResponse(resultSet, postgresMessage, resultsHandler);
    }

    private void handleQueryResponse(PostgresCachedResultSet resultSet, PostgresMessage postgresMessage,
                                     BiConsumer<Throwable, CachedResultSet> resultsHandler) {
        if (postgresMessage instanceof RowDescription) {
            handleRowDescription((RowDescription)postgresMessage, resultSet, resultsHandler);
        } else if (postgresMessage instanceof CommandComplete) {
            handleCommandComplete(resultsHandler, resultSet);
        } else if (postgresMessage instanceof DataRow) {
            DataRow dataRow = (DataRow) postgresMessage;
            handleDataRow(dataRow, resultSet, resultsHandler);
        } else if (postgresMessage instanceof ErrorResponse) {
            ErrorResponse errorResponse = (ErrorResponse) postgresMessage;
            resultsHandler.accept(errorResponse.toException(), null);
        }
    }

    private void handleCommandComplete(BiConsumer<Throwable, CachedResultSet> resultsHandler, PostgresCachedResultSet resultSet) {
        resultsHandler.accept(null, resultSet);
    }

    private void handleDataRow(DataRow dataRow, PostgresCachedResultSet resultSet, BiConsumer<Throwable, CachedResultSet> resultsHandler) {
        resultSet.addRow(dataRow.getColumnDatas());
        PostgresMessage.receive(sc, (throwable, postgresMessage1) -> {
            handleQueryResponse(resultSet, postgresMessage1, resultsHandler);
        });
    }

    private void handleRowDescription(RowDescription postgresMessage, PostgresCachedResultSet resultSet, BiConsumer<Throwable, CachedResultSet> resultsHandler) {
        resultSet.setFields(postgresMessage.getFields());
        PostgresMessage.receive(sc, (throwable, postgresMessage1) -> {
            handleQueryResponse(resultSet, postgresMessage1, resultsHandler);
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

    public Charset getClientEncodingCharset() {
        return Charset.forName(parameterMap.get(CLIENT_ENCODING_KEY));
    }
}
