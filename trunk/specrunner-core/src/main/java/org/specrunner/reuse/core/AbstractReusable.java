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
package org.specrunner.reuse.core;

import org.specrunner.reuse.IReusable;

/**
 * Partial reusable object implementation.
 * 
 * @param <T>
 *            The resource object type.
 * 
 * @author Thiago Santos
 */
public abstract class AbstractReusable<T> implements IReusable<T> {

    /**
     * A name given to the reusable resource.
     */
    protected String name;
    /**
     * The reusable object instance.
     */
    protected T object;

    /**
     * Create a reusable resource with name and the object to be reused.
     * 
     * @param name
     *            The name.
     * @param object
     *            The reusable object.
     */
    public AbstractReusable(String name, T object) {
        this.name = name;
        this.object = object;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the reusable object name.
     * 
     * @param name
     *            The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public T getObject() {
        return object;
    }

    /**
     * Sets the reusable object.
     * 
     * @param object
     *            The object.
     */
    public void setObject(T object) {
        this.object = object;
    }
}