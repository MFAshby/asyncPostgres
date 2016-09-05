package com.ashbysoft.asyncdb.postgres.messages;

import com.ashbysoft.asyncdb.postgres.ColumnData;

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

    public ArrayList<ColumnData> getColumnDatas() {
        return columnDatas;
    }
}
