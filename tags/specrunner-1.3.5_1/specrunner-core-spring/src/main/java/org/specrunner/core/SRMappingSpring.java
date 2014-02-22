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
package org.specrunner.core;

import org.specrunner.ISRMapping;
import org.specrunner.util.UtilLog;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A Spring mapping for objects.
 * 
 * @author Thiago Santos
 * 
 */
public class SRMappingSpring implements ISRMapping {

    /**
     * Configuration.
     */
    private ApplicationContext context;

    /**
     * Create a group of services provided by SpecRunner.
     */
    public SRMappingSpring() {
    }

    /**
     * Gets the default instance of a given services.
     * 
     * @param <T>
     *            The class type.
     * @param type
     *            The type.
     * @return The service object.
     */
    public <T> T getDefault(Class<T> type) {
        long time = System.currentTimeMillis();
        Object result = null;
        if (context == null) {
            context = new ClassPathXmlApplicationContext("applicationContext-SR.xml");
        }
        result = context.getBean(type);
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Get default(" + type + ") time: " + (System.currentTimeMillis() - time));
        }
        return type.cast(result);
    }
}