package com.ashbysoft.asyncdb.postgres.messages;

import java.nio.ByteBuffer;

public class PasswordMessage extends PostgresMessage {
    public static final char IDENTIFIER = 'p';

    private byte[] passwordBytes = null;

    public PasswordMessage(byte[] passwordBytes) {
        super(IDENTIFIER);
        this.passwordBytes = passwordBytes;
    }

    @Override
    protected int getLength() {
        return passwordBytes.length;
    }

    @Override
    protected void writePayloadToBuffer(ByteBuffer bb) {
        bb.put(passwordBytes);
    }

    @Override
    protected void readPayloadFromBuffer(ByteBuffer payloadBuffer) {
        throw new UnsupportedOperationException("Reading password message not supported");
    }
}
