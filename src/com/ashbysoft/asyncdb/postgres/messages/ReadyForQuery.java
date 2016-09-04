package com.ashbysoft.asyncdb.postgres.messages;

import java.nio.ByteBuffer;

public class ReadyForQuery extends PostgresMessage {
    public static final char IDENTIFIER = 'Z';

    public static final byte TRANSACTION_STATUS_IDLE = 'I';
    public static final byte TRANSACTION_STATUS_BLOCK = 'T';
    public static final byte TRANSACTION_STATUS_ERROR = 'E';

    private byte transactionStatus = -1;

    public ReadyForQuery() {
        super(IDENTIFIER);
    }

    @Override
    protected int getLength() {
        throw new UnsupportedOperationException("getLength not supported on ReadyForQuery");
    }

    @Override
    protected void writePayloadToBuffer(ByteBuffer bb) {
        throw new UnsupportedOperationException("writePayloadToBuffer not supported on ReadyForQuery");
    }

    @Override
    protected void readPayloadFromBuffer(ByteBuffer payloadBuffer) {
        transactionStatus = payloadBuffer.get();
    }

    public byte getTransactionStatus() {
        return transactionStatus;
    }
}
