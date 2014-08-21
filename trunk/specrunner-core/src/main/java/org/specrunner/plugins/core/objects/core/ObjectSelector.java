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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.core.objects.AbstractPluginObject;
import org.specrunner.plugins.core.objects.IObjectManager;
import org.specrunner.plugins.core.objects.IObjectSelector;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.RowAdapter;

/**
 * Memory object seeker.
 * 
 * @author Thiago Santos.
 * 
 */
public class ObjectSelector implements IObjectSelector<IObjectManager> {

    /**
     * Thread safe instance of <code>ObjecySelector</code>.
     */
    private static ThreadLocal<ObjectSelector> instance = new ThreadLocal<ObjectSelector>() {
        @Override
        protected ObjectSelector initialValue() {
            return new ObjectSelector();
        };
    };

    /**
     * Gets the thread safe instance of finder.
     * 
     * @return The finder instance.
     */
    public static ObjectSelector get() {
        return instance.get();
    }

    @Override
    public IObjectManager getSource(AbstractPluginObject caller, IContext context) throws Exception {
        return SRServices.get(IObjectManager.class);
    }

    @Override
    public Collection<Object> all(AbstractPluginObject caller, IContext context, IResultSet result) throws Exception {
        try {
            return SRServices.get(IObjectManager.class).lookup(caller.getTypeInstance());
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            return Collections.emptyList();
        }
    }

    @Override
    public List<Object> select(AbstractPluginObject caller, IContext context, Object instance, RowAdapter row, IResultSet result) throws Exception {
        try {
            Object lookup = getSource(caller, context).lookup(instance.getClass(), caller.makeKey(instance));
            return Arrays.asList(lookup);
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            return Collections.emptyList();
        }
    }

    @Override
    public void release() throws Exception {
        // nothing
    }
}