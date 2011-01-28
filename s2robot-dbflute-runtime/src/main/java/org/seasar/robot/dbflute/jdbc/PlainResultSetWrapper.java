/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.robot.dbflute.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * @author jflute
 */
public class PlainResultSetWrapper implements ResultSet {

    private ResultSet original;

    public PlainResultSetWrapper(ResultSet original) {
        this.original = original;
    }

    public int getConcurrency() throws SQLException {
        return original.getConcurrency();
    }

    public int getFetchDirection() throws SQLException {
        return original.getFetchDirection();
    }

    public int getFetchSize() throws SQLException {
        return original.getFetchSize();
    }

    public int getRow() throws SQLException {
        return original.getRow();
    }

    public int getType() throws SQLException {
        return original.getType();
    }

    public void afterLast() throws SQLException {
        original.afterLast();
    }

    public void beforeFirst() throws SQLException {
        original.beforeFirst();
    }

    public void cancelRowUpdates() throws SQLException {
        original.cancelRowUpdates();
    }

    public void clearWarnings() throws SQLException {
        original.clearWarnings();
    }

    public void close() throws SQLException {
        original.close();
    }

    public void deleteRow() throws SQLException {
        original.deleteRow();
    }

    public void insertRow() throws SQLException {
        original.insertRow();
    }

    public void moveToCurrentRow() throws SQLException {
        original.moveToCurrentRow();
    }

    public void moveToInsertRow() throws SQLException {
        original.moveToInsertRow();
    }

    public void refreshRow() throws SQLException {
        original.refreshRow();
    }

    public void updateRow() throws SQLException {
        original.updateRow();
    }

    public boolean first() throws SQLException {
        return original.first();
    }

    public boolean isAfterLast() throws SQLException {
        return original.isAfterLast();
    }

    public boolean isBeforeFirst() throws SQLException {
        return original.isBeforeFirst();
    }

    public boolean isFirst() throws SQLException {
        return original.isFirst();
    }

    public boolean isLast() throws SQLException {
        return original.isLast();
    }

    public boolean last() throws SQLException {
        return original.last();
    }

    public boolean next() throws SQLException {
        return original.next();
    }

    public boolean previous() throws SQLException {
        return original.previous();
    }

    public boolean rowDeleted() throws SQLException {
        return original.rowDeleted();
    }

    public boolean rowInserted() throws SQLException {
        return original.rowInserted();
    }

    public boolean rowUpdated() throws SQLException {
        return original.rowUpdated();
    }

    public boolean wasNull() throws SQLException {
        return original.wasNull();
    }

    public byte getByte(int columnIndex) throws SQLException {
        return original.getByte(columnIndex);
    }

    public double getDouble(int columnIndex) throws SQLException {
        return original.getDouble(columnIndex);
    }

    public float getFloat(int columnIndex) throws SQLException {
        return original.getFloat(columnIndex);
    }

    public int getInt(int columnIndex) throws SQLException {
        return original.getInt(columnIndex);
    }

    public long getLong(int columnIndex) throws SQLException {
        return original.getLong(columnIndex);
    }

    public short getShort(int columnIndex) throws SQLException {
        return original.getShort(columnIndex);
    }

    public void setFetchDirection(int direction) throws SQLException {
        original.setFetchDirection(direction);
    }

    public void setFetchSize(int rows) throws SQLException {
        original.setFetchSize(rows);
    }

    public void updateNull(int columnIndex) throws SQLException {
        original.updateNull(columnIndex);
    }

    public boolean absolute(int row) throws SQLException {
        return original.absolute(row);
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        return original.getBoolean(columnIndex);
    }

    public boolean relative(int rows) throws SQLException {
        return original.relative(rows);
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        return original.getBytes(columnIndex);
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        original.updateByte(columnIndex, x);

    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        original.updateDouble(columnIndex, x);
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        original.updateFloat(columnIndex, x);
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        original.updateInt(columnIndex, x);
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        original.updateLong(columnIndex, x);
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        original.updateShort(columnIndex, x);

    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        original.updateBoolean(columnIndex, x);
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        original.updateBytes(columnIndex, x);
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return original.getAsciiStream(columnIndex);
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return original.getBinaryStream(columnIndex);
    }

    /**
     * @param columnIndex
     * @return The value as InputStream.
     * @deprecated 
     * @throws SQLException
     */
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return original.getUnicodeStream(columnIndex);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        original.updateAsciiStream(columnIndex, x, length);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        original.updateBinaryStream(columnIndex, x, length);
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return original.getCharacterStream(columnIndex);
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        original.updateCharacterStream(columnIndex, x, length);
    }

    public Object getObject(int columnIndex) throws SQLException {
        return original.getObject(columnIndex);
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        original.updateObject(columnIndex, x);
    }

    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        original.updateObject(columnIndex, x, scale);
    }

    public String getCursorName() throws SQLException {
        return original.getCursorName();
    }

    public String getString(int columnIndex) throws SQLException {
        return original.getString(columnIndex);
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        original.updateString(columnIndex, x);
    }

    public byte getByte(String columnName) throws SQLException {
        return original.getByte(columnName);
    }

    public double getDouble(String columnName) throws SQLException {
        return original.getDouble(columnName);
    }

    public float getFloat(String columnName) throws SQLException {
        return original.getFloat(columnName);
    }

    public int findColumn(String columnName) throws SQLException {
        return original.findColumn(columnName);
    }

    public int getInt(String columnName) throws SQLException {
        return original.getInt(columnName);
    }

    public long getLong(String columnName) throws SQLException {
        return original.getLong(columnName);
    }

    public short getShort(String columnName) throws SQLException {
        return original.getShort(columnName);
    }

    public void updateNull(String columnName) throws SQLException {
        original.updateNull(columnName);
    }

    public boolean getBoolean(String columnName) throws SQLException {
        return original.getBoolean(columnName);
    }

    public byte[] getBytes(String columnName) throws SQLException {
        return original.getBytes(columnName);
    }

    public void updateByte(String columnName, byte x) throws SQLException {
        original.updateByte(columnName, x);
    }

    public void updateDouble(String columnName, double x) throws SQLException {
        original.updateDouble(columnName, x);
    }

    public void updateFloat(String columnName, float x) throws SQLException {
        original.updateFloat(columnName, x);
    }

    public void updateInt(String columnName, int x) throws SQLException {
        original.updateInt(columnName, x);
    }

    public void updateLong(String columnName, long x) throws SQLException {
        original.updateLong(columnName, x);
    }

    public void updateShort(String columnName, short x) throws SQLException {
        original.updateShort(columnName, x);
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException {
        original.updateBoolean(columnName, x);
    }

    public void updateBytes(String columnName, byte[] x) throws SQLException {
        original.updateBytes(columnName, x);
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return original.getBigDecimal(columnIndex);
    }

    /**
     * @param columnIndex
     * @param scale
     * @return The value as BigDecimal.
     * @deprecated 
     * @throws SQLException
     */
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return original.getBigDecimal(columnIndex, scale);
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {

        original.updateBigDecimal(columnIndex, x);
    }

    public URL getURL(int columnIndex) throws SQLException {
        return original.getURL(columnIndex);
    }

    public Array getArray(int i) throws SQLException {
        return original.getArray(i);
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        original.updateArray(columnIndex, x);
    }

    public Blob getBlob(int i) throws SQLException {
        return original.getBlob(i);
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        original.updateBlob(columnIndex, x);
    }

    public Clob getClob(int i) throws SQLException {
        return original.getClob(i);
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        original.updateClob(columnIndex, x);
    }

    public Date getDate(int columnIndex) throws SQLException {
        return original.getDate(columnIndex);
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        original.updateDate(columnIndex, x);
    }

    public Ref getRef(int i) throws SQLException {
        return original.getRef(i);
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        original.updateRef(columnIndex, x);
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return original.getMetaData();
    }

    public SQLWarning getWarnings() throws SQLException {
        return original.getWarnings();
    }

    public Statement getStatement() throws SQLException {
        return original.getStatement();
    }

    public Time getTime(int columnIndex) throws SQLException {
        return original.getTime(columnIndex);
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        original.updateTime(columnIndex, x);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return original.getTimestamp(columnIndex);
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        original.updateTimestamp(columnIndex, x);
    }

    public InputStream getAsciiStream(String columnName) throws SQLException {
        return original.getAsciiStream(columnName);
    }

    public InputStream getBinaryStream(String columnName) throws SQLException {
        return original.getBinaryStream(columnName);
    }

    /**
     * @param columnName
     * @return The value as InputStream.
     * @deprecated 
     * @throws SQLException
     */
    public InputStream getUnicodeStream(String columnName) throws SQLException {
        return original.getUnicodeStream(columnName);
    }

    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        original.updateAsciiStream(columnName, x, length);
    }

    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
        original.updateBinaryStream(columnName, x, length);
    }

    public Reader getCharacterStream(String columnName) throws SQLException {
        return original.getCharacterStream(columnName);
    }

    public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
        original.updateCharacterStream(columnName, reader, length);
    }

    public Object getObject(String columnName) throws SQLException {
        return original.getObject(columnName);
    }

    public void updateObject(String columnName, Object x) throws SQLException {
        original.updateObject(columnName, x);
    }

    public void updateObject(String columnName, Object x, int scale) throws SQLException {

        original.updateObject(columnName, x, scale);
    }

    @SuppressWarnings("unchecked")
    public Object getObject(int i, Map map) throws SQLException {
        return original.getObject(i, map);
    }

    public String getString(String columnName) throws SQLException {
        return original.getString(columnName);
    }

    public void updateString(String columnName, String x) throws SQLException {
        original.updateString(columnName, x);
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        return original.getBigDecimal(columnName);
    }

    /**
     * @param columnName
     * @param scale
     * @return The value as BigDecimal.
     * @deprecated 
     * @throws SQLException
     */
    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        return original.getBigDecimal(columnName, scale);
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {

        original.updateBigDecimal(columnName, x);
    }

    public URL getURL(String columnName) throws SQLException {
        return original.getURL(columnName);
    }

    public Array getArray(String colName) throws SQLException {
        return original.getArray(colName);
    }

    public void updateArray(String columnName, Array x) throws SQLException {
        original.updateArray(columnName, x);
    }

    public Blob getBlob(String colName) throws SQLException {
        return original.getBlob(colName);
    }

    public void updateBlob(String columnName, Blob x) throws SQLException {
        original.updateBlob(columnName, x);
    }

    public Clob getClob(String colName) throws SQLException {
        return original.getClob(colName);
    }

    public void updateClob(String columnName, Clob x) throws SQLException {
        original.updateClob(columnName, x);
    }

    public Date getDate(String columnName) throws SQLException {
        return original.getDate(columnName);
    }

    public void updateDate(String columnName, Date x) throws SQLException {
        original.updateDate(columnName, x);
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return original.getDate(columnIndex, cal);
    }

    public Ref getRef(String colName) throws SQLException {
        return original.getRef(colName);
    }

    public void updateRef(String columnName, Ref x) throws SQLException {
        original.updateRef(columnName, x);
    }

    public Time getTime(String columnName) throws SQLException {
        return original.getTime(columnName);
    }

    public void updateTime(String columnName, Time x) throws SQLException {
        original.updateTime(columnName, x);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return original.getTime(columnIndex, cal);
    }

    public Timestamp getTimestamp(String columnName) throws SQLException {
        return original.getTimestamp(columnName);
    }

    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        original.updateTimestamp(columnName, x);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return original.getTimestamp(columnIndex, cal);
    }

    @SuppressWarnings("unchecked")
    public Object getObject(String colName, Map map) throws SQLException {
        return original.getObject(colName, map);
    }

    public Date getDate(String columnName, Calendar cal) throws SQLException {
        return original.getDate(columnName, cal);
    }

    public Time getTime(String columnName, Calendar cal) throws SQLException {
        return original.getTime(columnName, cal);
    }

    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        return original.getTimestamp(columnName, cal);
    }
}