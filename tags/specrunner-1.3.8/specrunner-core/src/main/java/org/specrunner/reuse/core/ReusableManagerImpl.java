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
package org.specrunner.reuse.core;

import java.util.HashMap;

import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.util.UtilLog;

/**
 * Default reusable manager implementation.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ReusableManagerImpl extends HashMap<String, IReusable<?>> implements IReuseManager {

    @Override
    public IReusable<?> put(String name, IReusable<?> resource) {
        IReusable<?> ir = get(name);
        if (ir != null && ir.getObject() != resource.getObject()) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Release old reference:" + ir.getName() + " -> " + ir.getObject());
            }
            ir.release();
        }
        return super.put(name, resource);
    }

    @Override
    public void remove(String name) {
        IReusable<?> ir = get(name);
        if (ir != null) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Release:" + ir.getName() + " -> " + ir.getObject());
            }
            ir.release();
            remove(ir);
        }
    }
}