package org.specrunner.sql.proxy.jdbc;

import java.sql.CallableStatement;

import org.specrunner.sql.proxy.IFactoryJdbcAware;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface ICallableStatement extends CallableStatement, IFactoryJdbcAware {
}