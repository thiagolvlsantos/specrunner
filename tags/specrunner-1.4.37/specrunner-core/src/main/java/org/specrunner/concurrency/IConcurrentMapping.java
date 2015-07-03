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
package org.specrunner.concurrency;

/**
 * Given a resource by name, returns the equivalent within a concurrent
 * environment, for example, when asked to a database url as
 * <code>IConcurrentMapping.get("url","jdbc:hsqld:mem:test")</code> could return
 * <code>"jdbc:hsqld:mem:testThread1"</code>.
 * 
 * @author Thiago Santos
 * 
 */
public interface IConcurrentMapping {

    /**
     * Given a resource name and the original one, return a concurrent version
     * of this resource.
     * 
     * @param name
     *            The resource name.
     * @param value
     *            The resource value.
     * @return The changed resource.
     */
    Object get(String name, Object value);

    /**
     * Get the thread name normalized.
     * 
     * @return The thread name.
     */
    String getThread();
}