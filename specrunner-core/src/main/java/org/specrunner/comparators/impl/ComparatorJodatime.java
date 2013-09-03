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
package org.specrunner.comparators.impl;

import org.joda.time.ReadableInstant;

/**
 * Comparator of <code>ReadableInstant</code>s.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ComparatorJodatime extends AbstractComparatorTime {

    @Override
    public Class<?> getType() {
        return ReadableInstant.class;
    }

    @Override
    public boolean match(Object expected, Object received) {
        if (expected instanceof ReadableInstant && received instanceof ReadableInstant) {
            ReadableInstant left = (ReadableInstant) expected;
            ReadableInstant right = (ReadableInstant) received;
            return compare(left.getMillis(), right.getMillis());
        }
        return false;
    }

    @Override
    public int compare(Object expected, Object received) {
        if (expected instanceof ReadableInstant && received instanceof ReadableInstant) {
            ReadableInstant left = (ReadableInstant) expected;
            ReadableInstant right = (ReadableInstant) received;
            return (int) (left.getMillis() - right.getMillis());
        }
        return 0;
    }
}