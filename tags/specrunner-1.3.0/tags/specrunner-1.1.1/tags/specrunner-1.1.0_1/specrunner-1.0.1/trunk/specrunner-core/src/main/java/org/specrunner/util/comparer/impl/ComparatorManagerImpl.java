/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.util.comparer.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.specrunner.SpecRunnerServices;
import org.specrunner.properties.IPropertyLoader;
import org.specrunner.util.UtilLog;
import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.comparer.IComparatorManager;

/**
 * Default comparator manager implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ComparatorManagerImpl implements IComparatorManager {

    protected Map<String, IComparator> iComparators = new HashMap<String, IComparator>();
    protected IComparator defaultComparator = new ComparatorDefault();
    protected boolean initialized = false;

    public void initialize() {
        if (!initialized) {
            try {
                Properties p = SpecRunnerServices.get(IPropertyLoader.class).load("plugin_comparator.properties");
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("features=" + p);
                }
                for (Entry<Object, Object> e : p.entrySet()) {
                    String key = String.valueOf(e.getKey());
                    @SuppressWarnings("unchecked")
                    Class<? extends IComparator> c = (Class<? extends IComparator>) Class.forName(p.getProperty(key));
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("put(" + key + "," + c + ")");
                    }
                    iComparators.put(key, c.newInstance());
                }
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
            initialized = true;
        }
    }

    @Override
    public void bind(String name, IComparator iComparator) {
        initialize();
        iComparators.put(name, iComparator);
    }

    @Override
    public IComparator get(String name) {
        initialize();
        IComparator c = iComparators.get(name);
        if (c != null) {
            c.initialize();
        }
        return c;
    }

    @Override
    public IComparator get(Class<?> type) {
        initialize();
        for (IComparator c : iComparators.values()) {
            if (c.getType() != Object.class && c.getType().isAssignableFrom(type)) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Comparator for " + type.getName() + " is " + c);
                }
                c.initialize();
                return c;
            }
        }
        return null;
    }

    @Override
    public IComparator getDefaultComparator() {
        if (defaultComparator != null) {
            defaultComparator.initialize();
        }
        return defaultComparator;
    }

    @Override
    public void setDefaultComparator(IComparator defaultComparator) {
        this.defaultComparator = defaultComparator;
    }
}