package org.specrunner.plugins.core.objects;

import java.util.Map;

import org.specrunner.plugins.PluginException;

/**
 * Defined a repository of objects.
 * 
 * @author Thiago Santos.
 * 
 */
public interface IObjectManager {

    /**
     * Check if a given class is bound to a AbstractPluginObject.
     * 
     * @param clazz
     *            The object type.
     * @return true, of bound, false, otherwise.
     */
    boolean isBound(Class<?> clazz);

    /**
     * Bind a object plugin to the manager.
     * 
     * @param input
     *            The object plugin.
     */
    void bind(AbstractPluginObject input);

    /**
     * Lookup for a object of a given type, with the given key.
     * 
     * @param clazz
     *            The object type.
     * @param key
     *            The object key.
     * @return The object if exists,null, otherwise.
     * @throws PluginException
     *             On lookup errors.
     */
    Object lookup(Class<?> clazz, String key) throws PluginException;

    /**
     * The mapping of all entities.
     * 
     * @return The entity mapping.
     */
    Map<Class<?>, AbstractPluginObject> getEntities();

    /**
     * Clear all object mappings.
     */
    void clear();
}