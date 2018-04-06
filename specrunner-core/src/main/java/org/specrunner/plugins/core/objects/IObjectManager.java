/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.plugins.core.objects;

import java.util.List;
import java.util.Map;

import org.specrunner.plugins.PluginException;
import org.specrunner.util.functions.IPredicate;

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
     * Bind an object into memory repository.
     * 
     * @param clazz
     *            The object type.
     * @param key
     *            The object key.
     * @param instance
     *            The instance to be bound.
     */
    <T> void bind(Class<T> clazz, String key, T instance);

    /**
     * Lookup for a object of a given type, with the given key.
     * 
     * @param clazz
     *            The object type.
     * @return All handled objects, if exists, null, otherwise.
     * @throws PluginException
     *             On lookup errors.
     */
    <T> List<T> all(Class<T> clazz) throws PluginException;

    /**
     * Lookup for a object of a given type, filtered by a predicate.
     * 
     * @param clazz
     *            The object type.
     * @param filter
     *            The filter predicate.
     * @return All filtered objects, if exists, null, otherwise.
     * @throws PluginException
     *             On lookup errors.
     */
    <T> List<T> select(Class<T> clazz, IPredicate<T> filter) throws PluginException;

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
    <T> T get(Class<T> clazz, String key) throws PluginException;

    /**
     * Lookup for a object of a given type, with the given filter. Avoid filters
     * on keys, since they can be virtual.
     * 
     * @param clazz
     *            The object type.
     * @param filter
     *            The filter predicate.
     * @return The object if exists, null, otherwise.
     * @throws PluginException
     *             On lookup errors, i.e. more than 1 element returned by
     *             filter.
     */
    <T> T get(Class<T> clazz, IPredicate<T> filter) throws PluginException;

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
