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
package org.specrunner.comparators.core;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.specrunner.comparators.ComparatorException;

/**
 * Comparator of JVM dates and Joda Time readable instants.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ComparatorTime extends AbstractComparatorTime {

    @Override
    public Class<?> getType() {
        return Object.class;
    }

    @Override
    protected Long getMillis(Object obj) throws ComparatorException {
        if (obj == null) {
            return 0L;
        }
        if (obj instanceof Date) {
            return ((Date) obj).getTime();
        }
        if (obj instanceof ReadableInstant) {
            return ((ReadableInstant) obj).getMillis();
        }
        if (obj instanceof ReadablePartial) {
            return new DateTime((ReadablePartial) obj).getMillis();
        }
        throw new ComparatorException("Invalid object type for time comparison. " + obj.getClass() + " -> " + obj);
    }
}