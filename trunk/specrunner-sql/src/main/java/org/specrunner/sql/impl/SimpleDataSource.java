/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.sql.impl;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.specrunner.sql.IDataSourceProvider;
import org.specrunner.util.UtilLog;

/**
 * A very simple data source implementation to be used when only direct
 * connection information are provided.
 * 
 * @author Thiago Santos
 * 
 */
public class SimpleDataSource implements IDataSourceProvider, DataSource {

    /**
     * Current connection.
     */
    private Connection connection;

    /**
     * The driver.
     */
    private final String driver;
    /**
     * The URL.
     */
    private final String url;
    /**
     * The user.
     */
    private final String user;
    /**
     * The password.
     */
    private final String password;
    /**
     * The connection timeout.
     */
    private int timeout;

    /**
     * Creates a basic datasource.
     * 
     * @param driver
     *            A driver.
     * @param url
     *            A URL.
     * @param user
     *            A user.
     * @param password
     *            A password.
     */
    public SimpleDataSource(String driver, String url, String user, String password) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public DataSource getDataSource() {
        return this;
    }

    @Override
    public void release() {
        try {
            connection.close();
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Connection " + connection + " closed.");
            }
        } catch (SQLException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.timeout = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return timeout;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Method unsupported.");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return get(driver, url, user, password);
    }

    /**
     * Gets a connection from connection parameters.
     * 
     * @param driver
     *            The driver.
     * @param url
     *            The URL.
     * @param user
     *            The user.
     * @param pwd
     *            The password.
     * @return A connection.
     * @throws SQLException
     *             On connection errors.
     */
    protected Connection get(String driver, String url, String user, String pwd) throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                throw new SQLException(e);
            }
            connection = DriverManager.getConnection(url, user, pwd);
        }
        return connection;
    }

    @Override
    public String toString() {
        return SimpleDataSource.class.getSimpleName() + "(" + driver + "|" + url + "|" + user + "|" + password + ")";
    }
}