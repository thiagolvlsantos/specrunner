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
package org.specrunner.converters.core;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.specrunner.converters.ConverterException;

/**
 * Create current date (in timestamp).
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterTimestampCurrentTemplate extends AbstractConverterTimeTemplate<Timestamp> {

    /**
     * See superclass.
     * 
     * @param regexp
     *            Regexp.
     */
    public ConverterTimestampCurrentTemplate(String regexp) {
        super(regexp);
    }

    /**
     * See superclass.
     * 
     * @param values
     *            Value.
     */
    public ConverterTimestampCurrentTemplate(List<String> values) {
        super(values);
    }

    @Override
    protected Timestamp instance() {
        return new Timestamp(getCalendar().getTimeInMillis());
    }

    /**
     * Get a calendar object based on timestamp.
     * 
     * @return A calendar.
     */
    protected Calendar getCalendar() {
        Calendar calendar = null;
        TimeZone timeZone = getZone();
        if (timeZone == null) {
            calendar = Calendar.getInstance();
        } else {
            calendar = Calendar.getInstance(timeZone);
        }
        return calendar;
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value instanceof Timestamp) {
            return value;
        }
        return super.convert(value, args);
    }
}
