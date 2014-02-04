package org.specrunner.util.mapping;

import java.util.Map;

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
     * Binds a T object to a name.
     * 
     * @param name
     *            A name.
     * @param obj
     *            A comparator.
     * @return The mapping itself.
     */
    IMappingManager<T> bind(String name, T obj);
}
