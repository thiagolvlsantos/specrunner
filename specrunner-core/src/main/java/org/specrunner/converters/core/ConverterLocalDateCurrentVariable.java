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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.joda.time.LocalDate;

/**
 * Create current date + or - date information.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterLocalDateCurrentVariable extends ConverterLocalDateCurrent {

    protected Map<String, String> fieldToMethod = new HashMap<String, String>();
    protected Pattern pattern;

    public ConverterLocalDateCurrentVariable() {
        pattern = extractPattern(bindPatterns(fieldToMethod));
    }

    protected Map<String, String> bindPatterns(Map<String, String> map) {
        UtilJodatime.bindEnglishDate(map);
        UtilJodatime.bindPortugueseDate(map);
        return map;
    }

    protected Pattern extractPattern(Map<String, String> map) {
        return UtilJodatime.extractPattern(map);
    }

    @Override
    protected boolean testValue(String str, String value) {
        return str.startsWith(value);
    }

    @Override
    protected LocalDate postProcess(Object value, Object[] args, LocalDate result) {
        return UtilJodatime.postProcess(value, args, result, pattern, fieldToMethod);
    }
}