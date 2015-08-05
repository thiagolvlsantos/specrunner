package org.specrunner.util.mapping;

import java.util.Map;

import org.specrunner.util.reset.IResetable;

/**
 * Generic mapping.
 * 
 * @author Thiago Santos
 * 
 * @param <T>
 *            The mapped type.
 */
public interface IMappingManager<T extends IResetable> extends Map<String, T> {
    /**
     * The name of the default T.
     */
    String DEFAULT_NAME = "default";

    /**
     * Binds a T object to a name.
     * 
     * @param name
     *            A name.
     * @param obj
     *            A comparator.
     * @return The mapping itself.
     */
    IMappingManager<T> bind(String name, T obj);

    /**
     * Get the default object.
     * 
     * @return The default.
     */
    T getDefault();

    /**
     * Set default object.
     * 
     * @param obj
     *            Default object.
     */
    void setDefault(T obj);
}
