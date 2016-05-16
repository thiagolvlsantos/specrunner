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
package org.specrunner.converters.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.joda.time.LocalDate;
import org.specrunner.converters.ConverterException;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterLocalDateCurrentTemplate extends AbstractConverterTimeTemplate<LocalDate> {

    protected Map<String, String> aliasToField = new HashMap<String, String>();
    protected Map<String, String> fieldToMethod = new HashMap<String, String>();
    protected Pattern pattern;

    /**
     * See superclass.
     * 
     * @param values
     *            Value.
     */
    public ConverterLocalDateCurrentTemplate(List<String> values) {
        super(values);
        pattern = extractPattern(bindAliases(aliasToField).keySet());
        bindPatterns(fieldToMethod);
    }

    protected Map<String, String> bindAliases(Map<String, String> map) {
        UtilDate.bindDateAliases(map);
        return map;
    }

    protected Map<String, String> bindPatterns(Map<String, String> map) {
        UtilJodatime.bindDatePatterns(map);
        return map;
    }

    protected Pattern extractPattern(Set<String> set) {
        return UtilDate.extractPattern(set);
    }

    @Override
    protected boolean testValue(String str, String value) {
        return str.startsWith(value);
    }

    @Override
    protected LocalDate instance() {
        return new LocalDate();
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value instanceof LocalDate) {
            return value;
        }
        return super.convert(value, args);
    }

    @Override
    protected LocalDate postProcess(Object value, Object[] args, LocalDate result) {
        return UtilJodatime.postProcess(value, args, result, pattern, aliasToField, fieldToMethod);
    }
}
