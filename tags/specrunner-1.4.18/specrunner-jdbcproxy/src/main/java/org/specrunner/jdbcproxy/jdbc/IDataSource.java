package org.specrunner.jdbcproxy.jdbc;

import javax.sql.DataSource;

import org.specrunner.jdbcproxy.IFactoryJdbcAware;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IDataSource extends DataSource, IFactoryJdbcAware {
}