package org.specrunner.jdbcproxy;

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
