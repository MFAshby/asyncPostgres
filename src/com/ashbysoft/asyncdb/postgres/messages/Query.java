package com.ashbysoft.asyncdb.postgres.messages;

import com.ashbysoft.asyncdb.postgres.PostgresUtils;

import java.nio.ByteBuffer;

public class Query extends PostgresMessage {
    public static final char IDENTIFIER = 'Q';

    private byte[] queryBytes = null;

    public Query(String query) {
        super(IDENTIFIER);
        this.queryBytes = PostgresUtils.asciiBytesWithNull(query);
    }

    @Override
    protected int getLength() {
        return queryBytes.length;
    }

    @Override
    protected void writePayloadToBuffer(ByteBuffer bb) {
        bb.put(queryBytes);
    }

    @Override
    protected void readPayloadFromBuffer(ByteBuffer payloadBuffer) {
        throw new UnsupportedOperationException();
    }
}
