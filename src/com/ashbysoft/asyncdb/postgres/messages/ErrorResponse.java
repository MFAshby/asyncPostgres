package com.ashbysoft.asyncdb.postgres.messages;

import com.ashbysoft.asyncdb.postgres.PostgresUtils;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;

public class ErrorResponse extends PostgresMessage {
    public static final char IDENTIFIER = 'E';
    private ArrayList<ErrorDetail> errorDetails = new ArrayList<>();

    public ErrorResponse() {
        super(IDENTIFIER);
    }

    @Override
    protected int getLength() {
        throw new RuntimeException("Why am I writing an error response?");
    }

    @Override
    protected void writePayloadToBuffer(ByteBuffer bb) {
        throw new RuntimeException("Why am I writing an error response?");
    }

    @Override
    protected void readPayloadFromBuffer(ByteBuffer payloadBuffer) {
        while (payloadBuffer.hasRemaining()) {
            byte errorCode = payloadBuffer.get();
            if (errorCode != 0) {
                String errorMessage = PostgresUtils.nullTerminated(payloadBuffer);
                errorDetails.add(new ErrorDetail(errorCode, errorMessage));
            }
        }
    }

    public SQLException toException() {
        StringBuilder sb = new StringBuilder();
        errorDetails.stream()
                .map(String::valueOf)
                .forEach((s) -> {sb.append(s); sb.append('\n');});
        return new SQLException(sb.toString());
    }

    private static class ErrorDetail {
        private byte code;
        private String message;

        public ErrorDetail(byte code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String toString() {
            return String.format("Error code [%x] %s", code, message);
        }
    }
}
