package com.ashbysoft.asyncdb.postgres.messages;

import java.nio.ByteBuffer;

public class Terminate extends PostgresMessage {
    public static final char IDENTIFIER = 'X';

    public Terminate() {
        super(IDENTIFIER);
    }

    @Override
    protected int getLength() {
        return 0;
    }

    @Override
    protected void writePayloadToBuffer(ByteBuffer bb) {
        // No payload
    }

    @Override
    protected void readPayloadFromBuffer(ByteBuffer payloadBuffer) {
        throw new UnsupportedOperationException();
    }
}
