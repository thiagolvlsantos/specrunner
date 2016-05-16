/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.result;

import java.util.Map;

import org.specrunner.util.reset.IResetable;

/**
 * Writable object factory.
 * 
 * @author Thiago Santos
 * @param <T>
 *            The type of source to writable.
 * 
 */
public interface IWritableFactory<T> extends IResetable {

    /**
     * Get the type handled by this factory.
     * 
     * @return The source type.
     */
    Class<T> getType();

    /**
     * Writable with information.
     * 
     * @param source
     *            Source of information to writable objects.
     * @return The information.
     */
    IWritable newWritable(T source);

    /**
     * Writable with information.
     * 
     * @param information
     *            The information.
     * @param source
     *            Source of information to writable objects.
     * @return The information.
     */
    IWritable newWritable(Map<String, Object> information, T source);
}
