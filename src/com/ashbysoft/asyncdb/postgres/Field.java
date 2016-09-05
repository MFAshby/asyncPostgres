package com.ashbysoft.asyncdb.postgres;

import java.nio.ByteBuffer;
import java.util.Comparator;

public class Field {
    private String name;
    private int objectID;
    private short columnID;
    private int dataTypeID;
    private short dataTypeSize;
    private int typeModifier;
    private short formatCode;
    private int index;

    public static Field read(ByteBuffer bb, int index) {
        Field f = new Field();
        f.name = PostgresUtils.nullTerminated(bb);
        f.objectID = bb.getInt();
        f.columnID = bb.getShort();
        f.dataTypeID = bb.getInt();
        f.dataTypeSize = bb.getShort();
        f.typeModifier = bb.getInt();
        f.formatCode = bb.getShort();
        f.index = index;
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

    public int getIndex() {
        return index;
    }
}
