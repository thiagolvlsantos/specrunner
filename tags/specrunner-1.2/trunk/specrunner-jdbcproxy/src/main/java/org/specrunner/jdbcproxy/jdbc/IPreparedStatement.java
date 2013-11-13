package org.specrunner.jdbcproxy.jdbc;

import java.sql.PreparedStatement;

import org.specrunner.jdbcproxy.IFactoryJdbcAware;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPreparedStatement extends PreparedStatement, IFactoryJdbcAware {
}