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
package org.specrunner.concurrency.impl;

import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;

/**
 * Default implementation of IConcurrentMapping, returns a String concatenated
 * with thread name, does not matter the resource name.
 * 
 * @author Thiago Santos.
 * 
 */
public class ConcurrentMappingImpl implements IConcurrentMapping {

    @Override
    public Object get(String name, Object value) {
        String result = String.valueOf(value) + getThread();
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Mapping '" + name + "'='" + value + "' to '" + result);
        }
        return result;
    }

    @Override
    public String getThread() {
        return UtilString.normalize(Thread.currentThread().getName()).replace("-", "");
    }
}