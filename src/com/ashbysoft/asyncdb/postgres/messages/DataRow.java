package com.ashbysoft.asyncdb.postgres.messages;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class DataRow extends PostgresMessage {
    public static final char IDENTIFIER = 'D';

    private ArrayList<ColumnData> columnDatas = new ArrayList<>();

    public DataRow() {
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
        short numColumns = payloadBuffer.getShort();
        for (int i = 0; i < numColumns; i++) {
            columnDatas.add(ColumnData.read(payloadBuffer));
        }
    }

    public static class ColumnData {
        private int length;
        private byte[] data;

        public static ColumnData read(ByteBuffer bb) {
            ColumnData columnData = new ColumnData();
            columnData.length = bb.getInt();
            // -1 indicates NULL column value
            if (columnData.length > -1) {
                columnData.data = new byte[columnData.length];
                bb.get(columnData.data);
            }
            return columnData;
        }
    }

    public ArrayList<ColumnData> getColumnDatas() {
        return columnDatas;
    }
}
