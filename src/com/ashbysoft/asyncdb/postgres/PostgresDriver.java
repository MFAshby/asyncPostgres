package com.ashbysoft.asyncdb.postgres;

import com.ashbysoft.asyncdb.Connection;
import com.ashbysoft.asyncdb.Driver;
import com.ashbysoft.asyncdb.postgres.messages.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.function.BiConsumer;

import static com.ashbysoft.asyncdb.postgres.DefaultCompletionHandler.handle;
import static com.ashbysoft.asyncdb.postgres.PostgresUtils.asciiBytes;
import static com.ashbysoft.asyncdb.postgres.PostgresUtils.encodeMd5Password;

public class PostgresDriver implements Driver {
    public static final boolean DEBUG = true;
    @Override
    public void connect(String host, int port, String database, String username, String password,
                        BiConsumer<Connection, Throwable> connectedHandler) {
        try {
            AsynchronousSocketChannel sc = AsynchronousSocketChannel.open();
            sc.connect(new InetSocketAddress(host, port), null, handle((result, ex) -> {
                if (ex != null) {
                    connectedHandler.accept(null, ex);
                    return;
                }

                new StartupMessage(username, database).send(sc, (throwable, postgresMessage) -> {
                    if (throwable != null) {
                        connectedHandler.accept(null, throwable);
                        return;
                    }
                    handleStartResponse(postgresMessage, sc, username, password, connectedHandler);
                });
            }));
        } catch (IOException e) {
            connectedHandler.accept(null, e);
        }
    }

    private void handleStartResponse(PostgresMessage response, AsynchronousSocketChannel sc, String username,
                                     String password, BiConsumer<Connection, Throwable> connectedHandler) {
        if (response instanceof ErrorResponse) {
            handleError((ErrorResponse)response, connectedHandler);
        } else if (response instanceof AuthenticationResponse) {
            handleAuthResponse((AuthenticationResponse)response, sc, username, password, connectedHandler);
        } else {
            connectedHandler.accept(null, new SQLException("Unexpected response [" + response+ "]"));
        }
    }

    private void handleError(ErrorResponse response, BiConsumer<Connection, Throwable> connectedHandler) {
        connectedHandler.accept(null, response.toException());
    }

    private void handleAuthResponse(AuthenticationResponse response, AsynchronousSocketChannel sc, String username,
                                    String password, BiConsumer<Connection, Throwable> connectedHandler) {

        int authenticationResponse = response.getAuthenticationResponse();
        switch (authenticationResponse) {
            case AuthenticationResponse.AUTHENTICATION_OK:
                handleAuthenticationOK(sc, connectedHandler);
                break;
            case AuthenticationResponse.AUTHENTICATION_MD5:
                byte[] md5Salt = response.getMd5Salt();
                handleAuthenticationMd5Password(sc, md5Salt, connectedHandler, password, username);
                break;
            default:
                connectedHandler.accept(null, new SQLException("Unexpected authenticationResponse [" + authenticationResponse + "]"));
                break;
        }
    }

    private void handleAuthenticationOK(AsynchronousSocketChannel sc, BiConsumer<Connection, Throwable> connectedHandler) {
        PostgresConnection connection = new PostgresConnection(sc);
        handlePostAuthenticate(connection, connectedHandler);
    }

    private void handlePostAuthenticate(final PostgresConnection connection, BiConsumer<Connection, Throwable> connectedHandler) {
        PostgresMessage.receive(connection.getSc(), (throwable, postgresMessage) -> {
            if (postgresMessage instanceof ErrorResponse) {
                ErrorResponse errorResponse = (ErrorResponse) postgresMessage;
                connectedHandler.accept(null, errorResponse.toException());
            } else if (postgresMessage instanceof ReadyForQuery) {
                connectedHandler.accept(connection, null);
            } else if (postgresMessage instanceof ParameterStatus) {
                ParameterStatus parameterStatus = (ParameterStatus) postgresMessage;
                connection.addParameter(parameterStatus.getParameterName(), parameterStatus.getParameterValue());
                handlePostAuthenticate(connection, connectedHandler);
            } else if (postgresMessage instanceof BackendKeyData) {
                BackendKeyData backendKeyData = (BackendKeyData) postgresMessage;
                connection.setBackendKeyData(backendKeyData.getProcessId(), backendKeyData.getSecretKey());
                handlePostAuthenticate(connection, connectedHandler);
            }
        });
    }

    private void handleAuthenticationMd5Password(AsynchronousSocketChannel sc, byte[] salt,
                                                 BiConsumer<Connection, Throwable> connectedHandler, String password, String username) {
        try {
            byte[] md5Password = encodeMd5Password(asciiBytes(username), asciiBytes(password), salt);
            new PasswordMessage(md5Password).send(sc, ((throwable, postgresMessage) -> {
                if (throwable != null) {
                    connectedHandler.accept(null, throwable);
                    return;
                }
                handleStartResponse(postgresMessage, sc, username, password, connectedHandler);
            }));
        } catch (NoSuchAlgorithmException e) {
            connectedHandler.accept(null, e);
        }
    }

}
