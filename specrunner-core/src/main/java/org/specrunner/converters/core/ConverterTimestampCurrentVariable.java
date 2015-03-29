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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Create current date time + or - date/time information.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterTimestampCurrentVariable extends ConverterTimestampCurrent {

    protected Map<String, Integer> fieldToMethod = new HashMap<String, Integer>();
    protected Pattern pattern;

    public ConverterTimestampCurrentVariable() {
        pattern = extractPattern(bindPatterns(fieldToMethod).keySet());
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
    protected Timestamp postProcess(Object value, Object[] args, Timestamp result) {
        return UtilDate.postProcess(value, args, getCalendar(), result, pattern, fieldToMethod);
    }
}