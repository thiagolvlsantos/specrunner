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
package org.specrunner.converters.core;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.specrunner.converters.ConverterException;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDateCurrentTemplate extends AbstractConverterTimeTemplate<Date> {

    protected Map<String, String> aliasToField = new HashMap<String, String>();
    protected Map<String, Integer> fieldToMethod = new HashMap<String, Integer>();
    protected Pattern pattern;

    /**
     * See superclass.
     * 
     * @param values
     *            Value.
     */
    public ConverterDateCurrentTemplate(List<String> values) {
        super(values);
        pattern = extractPattern(bindAliases(aliasToField).keySet());
        bindPatterns(fieldToMethod);
    }

    protected Map<String, String> bindAliases(Map<String, String> map) {
        UtilDate.bindAliases(map);
        return map;
    }

    protected Map<String, Integer> bindPatterns(Map<String, Integer> map) {
        UtilDate.bindPatterns(map);
        return map;
    }

    protected Pattern extractPattern(Set<String> values) {
        return UtilDate.extractPattern(values);
    }

    @Override
    protected boolean testValue(String str, String value) {
        return str.startsWith(value);
    }

    @Override
    protected Date instance() {
        return getCalendar().getTime();
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
        if (value instanceof Date) {
            return value;
        }
        return super.convert(value, args);
    }

    @Override
    protected Date postProcess(Object value, Object[] args, Date result) {
        return UtilDate.postProcess(value, args, getCalendar(), result, pattern, aliasToField, fieldToMethod);
    }
}
