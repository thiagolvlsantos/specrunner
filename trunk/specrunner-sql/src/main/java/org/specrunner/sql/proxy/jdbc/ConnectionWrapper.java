package org.specrunner.sql.proxy.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

import org.specrunner.sql.proxy.AbstractFactoryJdbcAware;
import org.specrunner.sql.proxy.IFactoryJdbc;

/**
 * Wrapper of JDBC equivalent.
 * 
 * @author Thiago Santos
 * 
 */
public class ConnectionWrapper extends AbstractFactoryJdbcAware<Connection> implements IConnection {

    /**
     * Default constructor.
     * 
     * @param factory
     *            The factory.
     * @param original
     *            The wrapped object.
     */
    public ConnectionWrapper(IFactoryJdbc factory, Connection original) {
        super(factory, original);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return original.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return original.isWrapperFor(iface);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return factory.newStatement(original.createStatement());
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return factory.newPreparedStatement(original.prepareStatement(sql));
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return factory.newCallableStatement(original.prepareCall(sql));
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return original.nativeSQL(sql);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        original.setAutoCommit(autoCommit);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return original.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException {
        original.commit();
    }

    @Override
    public void rollback() throws SQLException {
        original.rollback();
    }

    @Override
    public void close() throws SQLException {
        original.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return original.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return original.getMetaData();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        original.setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return original.isReadOnly();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        original.setCatalog(catalog);
    }

    @Override
    public String getCatalog() throws SQLException {
        return original.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        original.setTransactionIsolation(level);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return original.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return original.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        original.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return factory.newStatement(original.createStatement(resultSetType, resultSetConcurrency));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return factory.newPreparedStatement(original.prepareStatement(sql, resultSetType, resultSetConcurrency));
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return factory.newCallableStatement(original.prepareCall(sql, resultSetType, resultSetConcurrency));
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return original.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        original.setTypeMap(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        original.setHoldability(holdability);
    }

    @Override
    public int getHoldability() throws SQLException {
        return original.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return original.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return original.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        original.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        original.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return factory.newStatement(original.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return factory.newPreparedStatement(original.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return factory.newCallableStatement(original.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return factory.newPreparedStatement(original.prepareStatement(sql, autoGeneratedKeys));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return factory.newPreparedStatement(original.prepareStatement(sql, columnIndexes));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return factory.newPreparedStatement(original.prepareStatement(sql, columnNames));
    }

    @Override
    public Clob createClob() throws SQLException {
        return original.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return original.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return original.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return original.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return original.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        original.setClientInfo(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        original.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return original.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return original.getClientInfo();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return original.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return original.createStruct(typeName, attributes);
    }
}
