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
package org.specrunner.util.composite.core;

import java.util.LinkedList;
import java.util.List;

import org.specrunner.util.composite.IComposite;

/**
 * Default composite implementation.
 * 
 * @author Thiago Santos
 * 
 * @param <P>
 *            The parent type.
 * @param <T>
 *            The children type.
 */
public class CompositeImpl<P extends IComposite<P, T>, T> implements IComposite<P, T> {

    /**
     * Children elements.
     */
    private final List<T> children = new LinkedList<T>();

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public List<T> getChildren() {
        return children;
    }

    @Override
    @SuppressWarnings("unchecked")
    public P add(T child) {
        children.add(child);
        return (P) this;
    }

}
