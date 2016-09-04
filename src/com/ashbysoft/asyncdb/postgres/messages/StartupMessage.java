package com.ashbysoft.asyncdb.postgres.messages;

import java.nio.ByteBuffer;

import static com.ashbysoft.asyncdb.postgres.PostgresUtils.asciiBytesWithNull;

public class StartupMessage extends PostgresMessage {
    private static final int PROTOCOL_VERSION = 196608;

    private byte[] USER_BYTES = asciiBytesWithNull("user");
    private byte[] DATABASE_BYTES = asciiBytesWithNull("database");

    private byte[] usernameBytes = null;
    private byte[] databaseBytes = null;

    public StartupMessage(String username, String database) {
        super('\0'); // No identifier for this message
        this.usernameBytes = asciiBytesWithNull(username);
        this.databaseBytes = asciiBytesWithNull(database);
    }

    @Override
    protected int getLength() {
        return 4  // Message length int32
                + 4     // Protocol version int32
                + USER_BYTES.length
                + usernameBytes.length
                + DATABASE_BYTES.length
                + databaseBytes.length
                + 1;    // Final null terminator
    }

    /**
     * The startup message is special and doesn't follow the same format as other messages.
     * Just override the write and do it ourselves.
     */
    @Override
    protected ByteBuffer writeToBuffer() {
        ByteBuffer byteByffer = ByteBuffer.allocateDirect(getLength());
        byteByffer.putInt(getLength());
        byteByffer.putInt(PROTOCOL_VERSION);
        byteByffer.put(USER_BYTES);
        byteByffer.put(usernameBytes);
        byteByffer.put(DATABASE_BYTES);
        byteByffer.put(databaseBytes);
        byteByffer.put((byte)0); // Extra null terminator to indicate we've finished.
        byteByffer.flip();
        return byteByffer;
    }

    @Override
    protected void writePayloadToBuffer(ByteBuffer bb) {
        throw new UnsupportedOperationException("writePayloadToBuffer Shouldn't be called for startup message");
    }

    @Override
    protected void readPayloadFromBuffer(ByteBuffer payloadBuffer) {
        throw new UnsupportedOperationException("Reading startup message not supported");
    }
}
