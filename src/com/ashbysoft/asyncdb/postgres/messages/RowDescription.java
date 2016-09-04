package com.ashbysoft.asyncdb.postgres.messages;

import com.ashbysoft.asyncdb.postgres.PostgresUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class RowDescription extends PostgresMessage {
    public static final char IDENTIFIER = 'T';

    private short numFields = -1;
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
        numFields = payloadBuffer.getShort();
        for (int i = 0; i < numFields; i++) {
            fields.add(Field.read(payloadBuffer));
        }
    }

    public static class Field {
        private String name;
        private int objectID;
        private short columnID;
        private int dataTypeID;
        private short dataTypeSize;
        private int typeModifier;
        private short formatCode;

        public static Field read(ByteBuffer bb) {
            Field f = new Field();
            f.name = PostgresUtils.nullTerminated(bb);
            f.objectID = bb.getInt();
            f.columnID = bb.getShort();
            f.dataTypeID = bb.getInt();
            f.dataTypeSize = bb.getShort();
            f.typeModifier = bb.getInt();
            f.formatCode = bb.getShort();
            return f;
        }

        public String getName() {
            return name;
        }

        public int getObjectID() {
            return objectID;
        }

        public short getColumnID() {
            return columnID;
        }

        public int getDataTypeID() {
            return dataTypeID;
        }

        public short getDataTypeSize() {
            return dataTypeSize;
        }

        public int getTypeModifier() {
            return typeModifier;
        }

        public short getFormatCode() {
            return formatCode;
        }
    }
}
