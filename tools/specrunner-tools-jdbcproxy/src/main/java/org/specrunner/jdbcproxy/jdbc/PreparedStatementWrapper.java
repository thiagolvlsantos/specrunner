package org.specrunner.jdbcproxy.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.specrunner.jdbcproxy.AbstractFactoryJdbcAware;
import org.specrunner.jdbcproxy.IFactoryJdbc;

/**
 * Wrapper of JDBC equivalent.
 * 
 * @author Thiago Santos
 * 
 */
public class PreparedStatementWrapper extends AbstractFactoryJdbcAware<PreparedStatement> implements IPreparedStatement {

    /**
     * Default constructor.
     * 
     * @param factory
     *            The factory.
     * @param original
     *            The wrapped object.
     */
    public PreparedStatementWrapper(IFactoryJdbc factory, PreparedStatement original) {
        super(factory, original);
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return factory.newResultSet(original.executeQuery(sql));
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return original.unwrap(iface);
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return factory.newResultSet(original.executeQuery());
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return original.executeUpdate(sql);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return original.isWrapperFor(iface);
    }

    @Override
    public int executeUpdate() throws SQLException {
        return original.executeUpdate();
    }

    @Override
    public void close() throws SQLException {
        original.close();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        original.setNull(parameterIndex, sqlType);
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return original.getMaxFieldSize();
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        original.setBoolean(parameterIndex, x);
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        original.setMaxFieldSize(max);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        original.setByte(parameterIndex, x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        original.setShort(parameterIndex, x);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return original.getMaxRows();
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        original.setInt(parameterIndex, x);
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        original.setMaxRows(max);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        original.setLong(parameterIndex, x);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        original.setEscapeProcessing(enable);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        original.setFloat(parameterIndex, x);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return original.getQueryTimeout();
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        original.setDouble(parameterIndex, x);
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        original.setQueryTimeout(seconds);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        original.setBigDecimal(parameterIndex, x);
    }

    @Override
    public void cancel() throws SQLException {
        original.cancel();
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        original.setString(parameterIndex, x);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return original.getWarnings();
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        original.setBytes(parameterIndex, x);
    }

    @Override
    public void clearWarnings() throws SQLException {
        original.clearWarnings();
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        original.setDate(parameterIndex, x);
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        original.setCursorName(name);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        original.setTime(parameterIndex, x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        original.setTimestamp(parameterIndex, x);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return original.execute(sql);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        original.setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return factory.newResultSet(original.getResultSet());
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        // CHECKSTYLE: OFF
        original.setUnicodeStream(parameterIndex, x, length);
        // CHECKSTYLE:ON
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return original.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return original.getMoreResults();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        original.setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        original.setFetchDirection(direction);
    }

    @Override
    public void clearParameters() throws SQLException {
        original.clearParameters();
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return original.getFetchDirection();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        original.setObject(parameterIndex, x, targetSqlType);
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        original.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return original.getFetchSize();
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        original.setObject(parameterIndex, x);
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return original.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return original.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        original.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        original.clearBatch();
    }

    @Override
    public boolean execute() throws SQLException {
        return original.execute();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return original.executeBatch();
    }

    @Override
    public void addBatch() throws SQLException {
        original.addBatch();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        original.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        original.setRef(parameterIndex, x);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return original.getConnection();
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        original.setBlob(parameterIndex, x);
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        original.setClob(parameterIndex, x);
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return original.getMoreResults(current);
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        original.setArray(parameterIndex, x);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return original.getMetaData();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return factory.newResultSet(original.getGeneratedKeys());
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        original.setDate(parameterIndex, x, cal);
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return original.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        original.setTime(parameterIndex, x, cal);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return original.executeUpdate(sql, columnIndexes);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        original.setTimestamp(parameterIndex, x, cal);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        original.setNull(parameterIndex, sqlType, typeName);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return original.executeUpdate(sql, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return original.execute(sql, autoGeneratedKeys);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        original.setURL(parameterIndex, x);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return original.getParameterMetaData();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        original.setRowId(parameterIndex, x);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return original.execute(sql, columnIndexes);
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        original.setNString(parameterIndex, value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        original.setNCharacterStream(parameterIndex, value, length);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return original.execute(sql, columnNames);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        original.setNClob(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        original.setClob(parameterIndex, reader, length);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return original.getResultSetHoldability();
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        original.setBlob(parameterIndex, inputStream, length);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return original.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        original.setPoolable(poolable);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        original.setNClob(parameterIndex, reader, length);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return original.isPoolable();
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        original.setSQLXML(parameterIndex, xmlObject);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        original.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        original.setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        original.setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        original.setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        original.setAsciiStream(parameterIndex, x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        original.setBinaryStream(parameterIndex, x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        original.setCharacterStream(parameterIndex, reader);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        original.setNCharacterStream(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        original.setClob(parameterIndex, reader);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        original.setBlob(parameterIndex, inputStream);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        original.setNClob(parameterIndex, reader);
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        original.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return original.isCloseOnCompletion();
    }
}
