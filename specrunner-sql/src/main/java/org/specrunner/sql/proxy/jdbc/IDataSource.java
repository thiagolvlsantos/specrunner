package org.specrunner.sql.proxy.jdbc;

import javax.sql.DataSource;

import org.specrunner.sql.proxy.IFactoryJdbcAware;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IDataSource extends DataSource, IFactoryJdbcAware {
}