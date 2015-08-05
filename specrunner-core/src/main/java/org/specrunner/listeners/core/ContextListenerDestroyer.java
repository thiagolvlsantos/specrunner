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

import java.util.Map;
import java.util.Map.Entry;

import org.specrunner.SpecRunnerException;
import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.context.IDestructable;
import org.specrunner.context.IModel;
import org.specrunner.util.UtilLog;

/**
 * Populator listener.
 * 
 * @author Thiago Santos
 * 
 */
public class ContextListenerDestroyer extends AbstractContextListener {

    @Override
    public String getName() {
        return "contextDestroyer";
    }

    @Override
    public void reset() {
    }

    @Override
    public void onDestroy(IContext context) {
        for (IBlock b : context) {
            Map<String, Object> objects = b.getMap();
            for (Entry<String, Object> o : objects.entrySet()) {
                Object value = o.getValue();
                if (value instanceof IModel<?>) {
                    try {
                        value = ((IModel<?>) value).getObject(context);
                    } catch (SpecRunnerException e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                    }
                }
                if (value instanceof IDestructable) {
                    IDestructable d = (IDestructable) value;
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Calling destroy in: " + d.getObject());
                    }
                    d.destroy();
                }
            }
        }
    }
}
