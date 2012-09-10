package org.specrunner.sql.proxy;

import java.sql.Statement;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IStatement extends Statement, IFactoryJdbcAware {
}