package com.ashbysoft.asyncdb.postgres.messages;

import java.nio.ByteBuffer;

public class BackendKeyData extends PostgresMessage {
    public static final char IDENTIFIER = 'K';

    private int processId = -1;
    private int secretKey = -1;

    public BackendKeyData() {
        super(IDENTIFIER);
    }

    @Override
    protected int getLength() {
        throw new UnsupportedOperationException("getLength not supported on BackendKeyData");
    }

    @Override
    protected void writePayloadToBuffer(ByteBuffer bb) {
        throw new UnsupportedOperationException("writePayloadToBuffer not supported on BackendKeyData");
    }

    @Override
    protected void readPayloadFromBuffer(ByteBuffer payloadBuffer) {
        processId = payloadBuffer.getInt();
        secretKey = payloadBuffer.getInt();
    }

    public int getProcessId() {
        return processId;
    }

    public int getSecretKey() {
        return secretKey;
    }
}
