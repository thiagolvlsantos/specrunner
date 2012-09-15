package org.specrunner.sql.proxy.jdbc;

import java.sql.Connection;

import org.specrunner.sql.proxy.IFactoryJdbcAware;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IConnection extends Connection, IFactoryJdbcAware {
}