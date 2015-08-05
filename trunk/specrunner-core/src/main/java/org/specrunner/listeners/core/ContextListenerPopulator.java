/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.listeners.core;

import java.util.Map.Entry;
import java.util.Properties;

import org.specrunner.context.IContext;
import org.specrunner.util.UtilLog;

/**
 * Populator listener.
 * 
 * @author Thiago Santos
 * 
 */
public class ContextListenerPopulator extends AbstractContextListener {

    @Override
    public String getName() {
        return "contextPopulator";
    }

    @Override
    public void reset() {
    }

    @Override
    public void onCreate(IContext context) {
        Properties p = System.getProperties();
        for (Entry<Object, Object> e : p.entrySet()) {
            String key = String.valueOf(e.getKey()).replace(".", "_");
            String value = String.valueOf(e.getValue());
            context.saveGlobal(key, value);
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(key + " mapped to '" + value + "'(String).");
            }
        }
    }
}
