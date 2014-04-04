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
package org.specrunner.comparators.core;

import java.sql.Timestamp;

/**
 * Comparator of timestamps.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ComparatorTimestamp extends AbstractComparatorTime {

    @Override
    public Class<?> getType() {
        return Timestamp.class;
    }

    @Override
    public boolean match(Object expected, Object received) {
        if (expected instanceof Timestamp && received instanceof Timestamp) {
            Timestamp left = (Timestamp) expected;
            Timestamp right = (Timestamp) received;
            return compare(left.getTime(), right.getTime());
        }
        return false;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof Timestamp && o2 instanceof Timestamp) {
            Timestamp left = (Timestamp) o1;
            Timestamp right = (Timestamp) o2;
            return left.compareTo(right);
        }
        return 0;
    }
}