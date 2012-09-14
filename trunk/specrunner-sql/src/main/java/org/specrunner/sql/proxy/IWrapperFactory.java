package org.specrunner.sql.proxy;

/**
 * Get a wrapper class for a type.
 * 
 * @author Thiago Santos
 * 
 */
public interface IWrapperFactory {

    /**
     * A wrapper class for the type.
     * 
     * @param <T>
     *            The wrapper class.
     * @param type
     *            The wrapper class.
     * @return The wrapper class.
     * @throws Exception
     *             On wrapper lookup errors.
     */
    <T> Class<T> wrapperClass(Class<T> type) throws Exception;
}
