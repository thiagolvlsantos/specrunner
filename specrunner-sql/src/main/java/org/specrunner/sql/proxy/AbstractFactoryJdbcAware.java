package org.specrunner.sql.proxy;


/**
 * Object abstraction.
 * 
 * @author Thiago Santos
 * 
 * @param <T>
 *            The wrapped object type.
 */
public class AbstractFactoryJdbcAware<T> implements IFactoryJdbcAware {

    /**
     * The factory.
     */
    protected IFactoryJdbc factory;
    /**
     * The wrapped object.
     */
    protected T original;

    /**
     * A JDBC aware object.
     * 
     * @param factory
     *            A factory.
     * @param original
     *            A wrappable object.
     */
    public AbstractFactoryJdbcAware(IFactoryJdbc factory, T original) {
        this.factory = factory;
        this.original = original;
    }

    @Override
    public IFactoryJdbc getFactory() {
        return factory;
    }

    /**
     * Sets the factory.
     * 
     * @param factory
     *            The factory.
     */
    public void setFactory(IFactoryJdbc factory) {
        this.factory = factory;
    }

    /**
     * Gets the object.
     * 
     * @return The wrapped object.
     */
    public T getOriginal() {
        return original;
    }

    /**
     * Sets the object.
     * 
     * @param original
     *            The wrapped object.
     */
    public void setOriginal(T original) {
        this.original = original;
    }

}