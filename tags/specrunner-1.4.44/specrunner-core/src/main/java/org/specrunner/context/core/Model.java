package org.specrunner.context.core;

import org.specrunner.SpecRunnerException;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;

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
    public static <T> IModel<T> of(final T source) {
        return new IModel<T>() {
            @Override
            public T getObject(IContext context) throws SpecRunnerException {
                return source;
            }
        };
    }
}
