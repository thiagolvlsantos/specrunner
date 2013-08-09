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
package org.specrunner.util.comparer.impl;

import org.specrunner.util.UtilLog;
import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.comparer.IComparatorManager;
import org.specrunner.util.mapping.impl.MappingManagerImpl;

/**
 * Default comparator manager implementation.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ComparatorManagerImpl extends MappingManagerImpl<IComparator> implements IComparatorManager {

    /**
     * Default constructor.
     */
    public ComparatorManagerImpl() {
        super("plugin_comparator.properties");
    }

    @Override
    public IComparator get(Class<?> type) {
        initialize();
        for (IComparator c : values()) {
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
        IComparator defaultComparator = getDefault();
        if (defaultComparator != null) {
            defaultComparator.initialize();
        }
        return defaultComparator;
    }

    @Override
    public void setDefaultComparator(IComparator defaultComparator) {
        bind(DEFAULT_NAME, defaultComparator);
    }
}