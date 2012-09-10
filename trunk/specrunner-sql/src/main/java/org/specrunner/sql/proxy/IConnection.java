package org.specrunner.sql.proxy;

import java.sql.Connection;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IConnection extends Connection, IFactoryJdbcAware {
}