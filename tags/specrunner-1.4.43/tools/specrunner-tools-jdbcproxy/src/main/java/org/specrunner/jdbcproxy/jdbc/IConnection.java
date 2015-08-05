package org.specrunner.jdbcproxy.jdbc;

import java.sql.Connection;

import org.specrunner.jdbcproxy.IFactoryJdbcAware;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IConnection extends Connection, IFactoryJdbcAware {
}
