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
package org.specrunner.plugins.core.objects.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.objects.AbstractPluginObject;
import org.specrunner.plugins.core.objects.IObjectManager;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;
import org.specrunner.util.functions.IPredicate;
import org.specrunner.util.xom.node.RowAdapter;

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
    public <T> void bind(Class<T> clazz, String key, T instance) {
        AbstractPluginObject old = entities.get(clazz);
        if (old == null) {
            old = new AbstractPluginObject() {
                @Override
                public ActionType getActionType() {
                    return Command.INSTANCE;
                }

                @Override
                protected void action(IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {
                    // for this method only in-memory saving.
                }
            };
            old.setTypeInstance(clazz);
            bind(old);
        }
        old.mapObject(instance, key, key);
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Bound (" + key + "," + instance + ")");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> all(Class<T> clazz) throws PluginException {
        AbstractPluginObject map = entities.get(clazz);
        if (map == null) {
            throw new PluginException("Object mapping for type " + clazz.getName() + " not found.");
        }
        return (List<T>) map.getObjects();
    }

    @Override
    public <T> List<T> select(Class<T> clazz, IPredicate<T> filter) throws PluginException {
        List<T> tmp = all(clazz);
        List<T> result = new LinkedList<T>();
        for (T t : tmp) {
            if (filter.apply(t)) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public <T> T get(Class<T> clazz, String key) throws PluginException {
        AbstractPluginObject map = entities.get(clazz);
        if (map == null) {
            throw new PluginException("Object mapping for type " + clazz.getName() + " not found.");
        }
        return clazz.cast(map.getObject(key));
    }

    @Override
    public <T> T get(Class<T> clazz, IPredicate<T> filter) throws PluginException {
        List<T> tmp = select(clazz, filter);
        if (tmp.isEmpty()) {
            return null;
        }
        if (tmp.size() > 1) {
            throw new PluginException("Multiple elements for " + clazz.getName() + " and filter " + filter + ".");
        }
        return tmp.get(0);
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
