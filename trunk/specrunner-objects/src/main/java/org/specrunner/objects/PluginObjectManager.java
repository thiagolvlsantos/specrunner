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
package org.specrunner.objects;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilLog;

/**
 * Manages the set of AbstractPluginObject created on a given specification.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginObjectManager {

    /**
     * Thread instance of manager.
     */
    private static ThreadLocal<PluginObjectManager> instance = new ThreadLocal<PluginObjectManager>() {
        @Override
        protected PluginObjectManager initialValue() {
            return new PluginObjectManager();
        };
    };

    /**
     * Map of instances for a given entity.
     */
    protected Map<Class<?>, AbstractPluginObject> entities = new HashMap<Class<?>, AbstractPluginObject>();

    /**
     * The object manager instance (Thread safe).
     * 
     * @return The instance.
     */
    public static PluginObjectManager get() {
        return instance.get();
    }

    /**
     * Clear all object mappings.
     */
    public void clear() {
        entities.clear();
    }

    /**
     * Check if a given class is bound to a AbstractPluginObject.
     * 
     * @param clazz
     *            The object type.
     * @return true, of bound, false, otherwise.
     */
    public boolean isBound(Class<?> clazz) {
        return entities.keySet().contains(clazz);
    }

    /**
     * Bind a object plugin to the manager.
     * 
     * @param input
     *            The object plugin.
     */
    public void bind(AbstractPluginObject input) {
        AbstractPluginObject old = entities.get(input.getTypeInstance());
        if (old == null) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Plugin " + input.getTypeInstance() + " is new.");
            }
            entities.put(input.getTypeInstance(), input);
        } else {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Plugin " + input.getTypeInstance() + " merge.");
            }
            entities.put(input.getTypeInstance(), input.merge(old));
        }
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Plugin of " + input.getTypeInstance() + " bound.");
        }
    }

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
    public Object lookup(Class<?> clazz, String key) throws PluginException {
        return entities.get(clazz).getObject(key);
    }

    /**
     * The mapping of all entities.
     * 
     * @return The entity mapping.
     */
    public Map<Class<?>, AbstractPluginObject> getEntities() {
        return entities;
    }
}