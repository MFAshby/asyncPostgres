package com.ashbysoft.asyncdb.postgres;


import com.ashbysoft.asyncdb.CachedResultSet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

public class PostgresCachedResultSet implements CachedResultSet {
    private static final int FORMAT_CODE_TEXT = 0;
    private static final int FORMAT_CODE_BINARY = 1;

    private PostgresConnection connection = null;
    private HashMap<String, Field> fields = new HashMap<>();
    private ArrayList<ArrayList<ColumnData>> columnData = new ArrayList<>();

    public PostgresCachedResultSet(PostgresConnection connection) {
        this.connection = connection;
    }

    public void setFields(Collection<Field> fields) {
        fields.forEach((f) -> this.fields.put(f.getName(), f));
    }

    public void addRow(ArrayList<ColumnData> r) {
        columnData.add(r);
    }

    @Override
    public int getRowCount() {
        return columnData.size();
    }

    @Override
    public String getString(int row, String columnName) {
        byte[] byteArray = getRawBytes(row, columnName);
        if (byteArray == null) {
            return null;
        }
        return new String(byteArray, connection.getClientEncodingCharset());
    }

    @Override
    public Integer getInt(int row, String columnName) {
        String string = getString(row, columnName);
        if (string == null)
            return null;
        return Integer.valueOf(string);
    }

    @Override
    public byte[] getByteArray(int row, String columnName) {
        byte[] data = getRawBytes(row, columnName);
        return PGDataConvert.toBytes(data);
    }

    private byte[] getRawBytes(int row, String columnName) {
        Field field = fields.get(columnName);
        if (field == null) {
            throw new RuntimeException("Unknown field [" + columnName + "]");
        }

        int columnID = field.getIndex();
        ArrayList<ColumnData> columnDatas = columnData.get(row);
        ColumnData columnData = columnDatas.get(columnID);
        return columnData.getData();
    }
}
