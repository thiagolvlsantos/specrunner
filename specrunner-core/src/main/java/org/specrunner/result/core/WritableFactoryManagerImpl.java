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
package org.specrunner.result.core;

import org.specrunner.result.IWritableFactory;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.util.UtilLog;
import org.specrunner.util.mapping.core.MappingManagerImpl;

/**
 * Default writable manager implementation.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class WritableFactoryManagerImpl extends MappingManagerImpl<IWritableFactory<?>> implements IWritableFactoryManager {

    /**
     * Default constructor.
     */
    public WritableFactoryManagerImpl() {
        super("sr_writables.properties");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> IWritableFactory<T> get(Class<T> type) {
        initialize();
        IWritableFactory<?> c = get(type.getName());
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("WritableFactory for " + type.getName() + " is " + c);
        }
        if (c != null) {
            c.initialize();
        }
        return (IWritableFactory<T>) c;
    }
}
