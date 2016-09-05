package com.ashbysoft.asyncdb.postgres;

import java.nio.ByteBuffer;

public class ColumnData {
    private byte[] data;

    public static ColumnData read(ByteBuffer bb) {
        ColumnData columnData = new ColumnData();
        int length = bb.getInt();
        // -1 indicates NULL column value
        if (length > -1) {
            columnData.data = new byte[length];
            bb.get(columnData.data);
        } else {
            columnData.data = null;
        }
        return columnData;
    }

    public byte[] getData() {
        return data;
    }
}
