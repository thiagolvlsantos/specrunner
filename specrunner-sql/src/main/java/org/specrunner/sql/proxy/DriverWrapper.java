package org.specrunner.sql.proxy;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Driver wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public class DriverWrapper extends AbstractFactoryJdbcAware<Driver> implements IDriver {

    /**
     * JDBC wrapper.
     */
    public static final String WRAPPER_PREFIX = "jdbc:sr:";

    /**
     * Default constructor.
     * 
     * @param factory
     *            The factory.
     */
    public DriverWrapper(IFactoryJdbc factory) {
        super(factory, null);
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
        if (url.startsWith(WRAPPER_PREFIX)) {
            return url.substring(WRAPPER_PREFIX.length());
        }
        return url;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith(WRAPPER_PREFIX) && original.acceptsURL(clean(url));
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