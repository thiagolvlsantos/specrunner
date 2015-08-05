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
package org.specrunner.util.composite;

import java.util.List;

/**
 * Basic composite interface.
 * 
 * @author Thiago Santos.
 * 
 * @param <P>
 *            The parent type.
 * @param <T>
 *            The children type.
 */
public interface IComposite<P extends IComposite<P, T>, T> {

    /**
     * Check if object has children.
     * 
     * @return true, if has none children, false, otherwise.
     */
    boolean isEmpty();

    /**
     * Get the children list.
     * 
     * @return The children list.
     */
    List<T> getChildren();

    /**
     * Add a child and return the object itself.
     * 
     * @param child
     *            The child.
     * @return The composite itself.
     */
    P add(T child);
}
