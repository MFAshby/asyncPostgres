package com.ashbysoft.asyncdb.postgres.messages;

import com.ashbysoft.asyncdb.postgres.Field;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class RowDescription extends PostgresMessage {
    public static final char IDENTIFIER = 'T';

    private ArrayList<Field> fields = new ArrayList<>();

    public RowDescription() {
        super(IDENTIFIER);
    }

    @Override
    protected int getLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writePayloadToBuffer(ByteBuffer bb) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void readPayloadFromBuffer(ByteBuffer payloadBuffer) {
        short numFields = payloadBuffer.getShort();
        for (int i = 0; i < numFields; i++) {
            Field field = Field.read(payloadBuffer, i);
            fields.add(field);
        }
    }

    public ArrayList<Field> getFields() {
        return fields;
    }
}
