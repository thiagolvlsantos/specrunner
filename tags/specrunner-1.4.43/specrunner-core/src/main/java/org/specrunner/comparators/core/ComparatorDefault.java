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
package org.specrunner.comparators.core;

import org.specrunner.comparators.IComparator;

/**
 * A default comparator. It compares nulls values, and if both are not null, it
 * uses <code>Object.equals(Object)</code>.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ComparatorDefault implements IComparator {

    @Override
    public Class<?> getType() {
        return Object.class;
    }

    @Override
    public void initialize() {
    }

    @Override
    public boolean match(Object expected, Object received) {
        if (expected instanceof Number && received instanceof Number) {
            return ((Number) expected).doubleValue() == ((Number) received).doubleValue();
        }
        return expected == null ? received == null : expected.equals(received);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public int compare(Object o1, Object o2) {
        if (o1 instanceof Comparable<?> && o2 instanceof Comparable<?>) {
            Comparable left = (Comparable) o1;
            Comparable right = (Comparable) o2;
            return left.compareTo(right);
        }
        return toString(o1).compareTo(toString(o2));
    }

    /**
     * Convert object to String.
     * 
     * @param obj
     *            An object.
     * @return A string for that object.
     */
    protected String toString(Object obj) {
        return String.valueOf(obj);
    }
}
