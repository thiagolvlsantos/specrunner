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
package org.specrunner.reuse;

import java.util.Map;

/**
 * Stands for a reusable object. Any reusable candidate object can implement
 * this interface and use the services of reusable items.
 * 
 * @param <T>
 *            The reusable object type.
 * @author Thiago Santos
 */
public interface IReusable<T> {

    /**
     * Reusable name.
     * 
     * @return The reusable resource name.
     */
    String getName();

    /**
     * The reusable object.
     * 
     * @return Object.
     */
    T getObject();

    /**
     * Return if the reusable can be reused. i.e. if a Jetty Server has the same
     * configuration file it can be reused.
     * 
     * @param cfg
     *            The object configuration elements.
     * @return true, if is reusable, false, otherwise.
     */
    boolean canReuse(Map<String, Object> cfg);

    /**
     * Reset reusable resources. For example, if the hold object is a Jetty
     * Server reset clear its resources.
     */
    void reset();

    /**
     * Release resource best effort.
     */
    void release();
}
