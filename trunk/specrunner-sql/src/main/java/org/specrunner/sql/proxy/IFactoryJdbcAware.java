package org.specrunner.sql.proxy;


/**
 * Abstraction for a factory aware component.
 * 
 * @author Thiago Santos
 * 
 */
public interface IFactoryJdbcAware {

    /**
     * Gets the factory.
     * 
     * @return The factory.
     */
    IFactoryJdbc getFactory();
}
