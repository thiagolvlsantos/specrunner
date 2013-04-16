package org.specrunner.context.impl;

import org.specrunner.SpecRunnerException;
import org.specrunner.context.IContext;

/**
 * Utility for models.
 * 
 * @author Thiago Santos.
 * 
 */
public final class Model {

    /**
     * Default constructor.
     */
    private Model() {
        super();
    }

    /**
     * Returns a model for any object.
     * 
     * @param <T>
     *            The object type.
     * @param source
     *            The object.
     * @return A model with the give object.
     */
    public static <T> AbstractReadOnlyModel<T> of(final T source) {
        return new AbstractReadOnlyModel<T>() {
            @Override
            public T getObject(IContext context) throws SpecRunnerException {
                return source;
            }
        };
    }
}