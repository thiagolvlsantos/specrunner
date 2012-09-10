package org.specrunner.sql.proxy;

import javax.sql.DataSource;

/**
 * Abstraction for a wrapper.
 * 
 * @author Thiago Santos
 * 
 */
public interface IDataSource extends DataSource, IFactoryJdbcAware {
}