package org.specrunner.sql.proxy.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.specrunner.sql.proxy.AbstractFactoryJdbcAware;
import org.specrunner.sql.proxy.IFactoryJdbc;

/**
 * Wrapper of JDBC equivalent.
 * 
 * @author Thiago Santos
 * 
 */
public class DataSourceWrapper extends AbstractFactoryJdbcAware<DataSource> implements IDataSource {

    /**
     * Default constructor.
     * 
     * @param factory
     *            The factory.
     * @param original
     *            The wrapped object.
     */
    public DataSourceWrapper(IFactoryJdbc factory, DataSource original) {
        super(factory, original);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return original.getLogWriter();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return original.unwrap(iface);
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        original.setLogWriter(out);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return original.isWrapperFor(iface);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        original.setLoginTimeout(seconds);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return factory.newConnection(original.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return factory.newConnection(original.getConnection(username, password));
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return original.getLoginTimeout();
    }
}