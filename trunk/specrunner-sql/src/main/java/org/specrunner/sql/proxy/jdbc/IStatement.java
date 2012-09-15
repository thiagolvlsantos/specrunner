package org.specrunner.sql.proxy.jdbc;

import java.sql.Statement;

import org.specrunner.sql.proxy.IFactoryJdbcAware;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IStatement extends Statement, IFactoryJdbcAware {
}