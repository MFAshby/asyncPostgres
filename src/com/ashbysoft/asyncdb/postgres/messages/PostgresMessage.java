package com.ashbysoft.asyncdb.postgres.messages;

import com.ashbysoft.asyncdb.postgres.PostgresDriver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.util.function.BiConsumer;

import static com.ashbysoft.asyncdb.postgres.DefaultCompletionHandler.handle;

/**
 * All messages to / from postgres follow a pattern, except the initial message which is slightly different.
 * Created by mfash on 04/09/2016.
 */
public abstract class PostgresMessage {
    private static final int HEADER_LEN = 5;

    private char identifier;

    public PostgresMessage(char identifier) {
        this.identifier = identifier;
    }

    protected abstract int getLength();
    protected abstract void writePayloadToBuffer(ByteBuffer bb);
    protected abstract void readPayloadFromBuffer(ByteBuffer payloadBuffer);

    public void send(AsynchronousSocketChannel sc, BiConsumer<Throwable, PostgresMessage> handler) {
        ByteBuffer byteBuffer = writeToBuffer();
        sc.write(byteBuffer, null, handle((count, ex) -> {
            if (ex != null) {
                handler.accept(ex, null);
                return;
            }

            receive(sc, handler);
        }));
    }

    protected ByteBuffer writeToBuffer() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(getLength() + 5); //Include space for length, and
        byteBuffer.put((byte)identifier);
        byteBuffer.putInt(getLength() + 4); //Length includes itself.
        writePayloadToBuffer(byteBuffer);
        byteBuffer.flip();

        if (PostgresDriver.DEBUG) {
            debugLogBuffer("Sending", byteBuffer);
        }

        return byteBuffer;
    }

    private static void debugLogBuffer(String title, ByteBuffer byteBuffer) {
        try {
            System.out.print(title);
            System.out.print(": [");
            byte[] b = new byte[byteBuffer.remaining()];
            byteBuffer.get(b);
            System.out.println(new String(b));
            System.out.println("]\n");
        } finally {
            byteBuffer.position(0);
        }
    }

    public static void receive(AsynchronousSocketChannel sc, BiConsumer<Throwable, PostgresMessage> handler) {
        ByteBuffer headerBuffer = ByteBuffer.allocateDirect(HEADER_LEN);
        sc.read(headerBuffer, null, handle((ignored, throwable) -> {
            if (throwable != null) {
                handler.accept(throwable, null);
                return;
            }

            headerBuffer.position(0);

            if (PostgresDriver.DEBUG) {
                debugLogBuffer("Received Header: ", headerBuffer);
            }
            char responseIdentifier = (char)headerBuffer.get();
            int responseLength = headerBuffer.getInt() - 4; // length includes length integer but not the identifier.
            ByteBuffer payloadBuffer = ByteBuffer.allocateDirect(responseLength);
            sc.read(payloadBuffer, null, handle((ignored2, throwable1) -> {
                if (throwable1 != null) {
                    handler.accept(throwable1, null);
                    return;
                }
                payloadBuffer.position(0);
                if (PostgresDriver.DEBUG) {
                    debugLogBuffer("Received Payload: ", payloadBuffer);
                }

                PostgresMessage responseMessage = PostgresMessage.factoryForIdentifier(responseIdentifier);
                assert responseMessage != null;
                responseMessage.readPayloadFromBuffer(payloadBuffer);

                handler.accept(null, responseMessage);
            }));
        }));
    }

    private static PostgresMessage factoryForIdentifier(char responseIdentifier) {
        switch (responseIdentifier) {
            case ErrorResponse.IDENTIFIER:
                return new ErrorResponse();
            case AuthenticationResponse.IDENTIFIER:
                return new AuthenticationResponse();
            case ParameterStatus.IDENTIFIER:
                return new ParameterStatus();
            case BackendKeyData.IDENTIFIER:
                return new BackendKeyData();
            case ReadyForQuery.IDENTIFIER:
                return new ReadyForQuery();
            case RowDescription.IDENTIFIER:
                return new RowDescription();
            case CommandComplete.IDENTIFIER:
                return new CommandComplete();
            case DataRow.IDENTIFIER:
                return new DataRow();
            default:
                throw new RuntimeException("Unexpected identifier [" + responseIdentifier + "]");
        }
    }
}
