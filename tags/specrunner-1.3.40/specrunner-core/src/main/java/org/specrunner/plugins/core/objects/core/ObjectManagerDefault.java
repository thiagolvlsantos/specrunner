/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.plugins.core.objects.core;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.objects.AbstractPluginObject;
import org.specrunner.plugins.core.objects.IObjectManager;
import org.specrunner.util.UtilLog;

/**
 * Manages the set of AbstractPluginObject created on a given specification.
 * 
 * @author Thiago Santos
 * 
 */
public class ObjectManagerDefault implements IObjectManager {

    /**
     * Map of instances for a given entity.
     */
    protected Map<Class<?>, AbstractPluginObject> entities = new HashMap<Class<?>, AbstractPluginObject>();

    @Override
    public boolean isBound(Class<?> clazz) {
        return entities.containsKey(clazz);
    }

    @Override
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

    @Override
    public Object lookup(Class<?> clazz, String key) throws PluginException {
        AbstractPluginObject map = entities.get(clazz);
        if (map == null) {
            throw new PluginException("Object mapping for type " + clazz.getName() + " not found.");
        }
        return map.getObject(key);
    }

    @Override
    public Map<Class<?>, AbstractPluginObject> getEntities() {
        return entities;
    }

    @Override
    public void clear() {
        entities.clear();
    }
}