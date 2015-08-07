package org.specrunner.jdbcproxy.jdbc;

import java.sql.CallableStatement;

import org.specrunner.jdbcproxy.IFactoryJdbcAware;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface ICallableStatement extends CallableStatement, IFactoryJdbcAware {
}
