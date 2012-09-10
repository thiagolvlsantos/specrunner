package org.specrunner.sql.proxy;

import java.sql.PreparedStatement;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPreparedStatement extends PreparedStatement, IFactoryJdbcAware {
}