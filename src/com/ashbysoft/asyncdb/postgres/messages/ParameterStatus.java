package com.ashbysoft.asyncdb.postgres.messages;

import com.ashbysoft.asyncdb.postgres.PostgresUtils;

import java.nio.ByteBuffer;

public class ParameterStatus extends PostgresMessage {
    public static final char IDENTIFIER = 'S';

    private String parameterName = null;
    private String parameterValue = null;

    public ParameterStatus() {
        super(IDENTIFIER);
    }

    @Override
    protected int getLength() {
        throw new UnsupportedOperationException("getLength not supported on ParameterStatus");
    }

    @Override
    protected void writePayloadToBuffer(ByteBuffer bb) {
        throw new UnsupportedOperationException("writePayloadToBuffer not supported on ParameterStatus");
    }

    @Override
    protected void readPayloadFromBuffer(ByteBuffer payloadBuffer) {
        parameterName = PostgresUtils.nullTerminated(payloadBuffer);
        parameterValue = PostgresUtils.nullTerminated(payloadBuffer);
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }
}
