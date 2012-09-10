package org.specrunner.sql.proxy;

import java.sql.CallableStatement;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface ICallableStatement extends CallableStatement, IFactoryJdbcAware {
}