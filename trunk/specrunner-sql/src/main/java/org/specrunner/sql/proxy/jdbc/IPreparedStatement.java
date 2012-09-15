package org.specrunner.sql.proxy.jdbc;

import java.sql.PreparedStatement;

import org.specrunner.sql.proxy.IFactoryJdbcAware;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPreparedStatement extends PreparedStatement, IFactoryJdbcAware {
}