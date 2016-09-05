package com.ashbysoft.asyncdb;

import java.sql.SQLException;

/**
 * ResultSet where all the data has already been received from the database query,
 * so there's no need for any async transport to fetch it
 */
public interface CachedResultSet {
    int getRowCount();
    String getString(int row, String columnName);
    Integer getInt(int row, String columnName);
    byte[] getByteArray(int row, String columnName);
}
