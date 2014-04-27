/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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