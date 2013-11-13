package org.specrunner.jdbcproxy.jdbc;

import java.sql.Statement;

import org.specrunner.jdbcproxy.IFactoryJdbcAware;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IStatement extends Statement, IFactoryJdbcAware {
}