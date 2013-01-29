package org.specrunner.jdbcproxy.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import org.specrunner.jdbcproxy.AbstractFactoryJdbcAware;
import org.specrunner.jdbcproxy.IFactoryJdbc;

/**
 * Driver wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public class DriverWrapper extends AbstractFactoryJdbcAware<Driver> implements IDriver {

    /**
     * The driver prefix.
     */
    private final String prefix;

    /**
     * Default constructor.
     * 
     * @param factory
     *            The factory.
     * @param prefix
     *            URL prefix.
     */
    public DriverWrapper(IFactoryJdbc factory, String prefix) {
        super(factory, null);
        this.prefix = prefix;
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        String str = clean(url);
        Enumeration<Driver> lista = DriverManager.getDrivers();
        while (lista.hasMoreElements()) {
            Driver tmp = lista.nextElement();
            if (tmp.acceptsURL(str)) {
                original = tmp;
                break;
            }
        }
        if (original == null) {
            throw new SQLException("None suitable original found for URL:" + str);
        }
        return factory.newConnection(original.connect(str, info));
    }

    /**
     * Remove original prefix.
     * 
     * @param url
     *            The url.
     * @return The url without proxy prefix.
     */
    protected String clean(String url) {
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        return url;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(prefix) && original.acceptsURL(clean(url));
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return original.getPropertyInfo(clean(url), info);
    }

    @Override
    public int getMajorVersion() {
        return original.getMajorVersion();
    }

    @Override
    public int getMinorVersion() {
        return original.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant() {
        return original.jdbcCompliant();
    }
}