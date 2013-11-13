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
package org.specrunner.context;

import org.specrunner.SpecRunnerException;

/**
 * Something that returns and sets a object.
 * 
 * @author Thiago Santos
 * 
 * @param <T>
 *            Input object type.
 * @param <U>
 *            Output object type.
 */
public interface IModel<T, U> {

    /**
     * Return the object.
     * 
     * @param context
     *            The context.
     * @return The object model.
     */
    U getObject(IContext context) throws SpecRunnerException;

    /**
     * Sets the model.
     * 
     * @param object
     *            The object model.
     * @param context
     *            The context.
     */
    void setObject(T object, IContext context) throws SpecRunnerException;
}