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
package org.specrunner.converters.core;

import java.util.Arrays;

import org.specrunner.converters.ConverterException;

/**
 * Create time information.
 * 
 * @author Thiago Santos
 * 
 * @param <T>
 *            Date object.
 */
@SuppressWarnings("serial")
public abstract class AbstractConverterTimeTemplate<T> extends AbstractConverterTimezone<T> {

    /**
     * Strings that stand for 'current date'.
     */
    private String[] values;
    /**
     * Pattern for 'current date'.
     */
    private String regexp;

    /**
     * Constructor using strings.
     * 
     * @param values
     *            The values to be converted to date.
     */
    public AbstractConverterTimeTemplate(String[] values) {
        this.values = values == null ? null : Arrays.copyOf(values, values.length);
    }

    /**
     * Constructor using a regular expression.
     * 
     * @param regexp
     *            The regular expression to match date.
     */
    public AbstractConverterTimeTemplate(String regexp) {
        this.regexp = regexp;
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        String str = String.valueOf(value);
        if (regexp != null && str.matches(regexp)) {
            return instance();
        }
        if (values != null) {
            for (String v : values) {
                if (str.contains(v)) {
                    return instance();
                }
            }
        }
        throw new ConverterException("Invalid value '" + value + "'.");
    }

    /**
     * Creates an instance of date.
     * 
     * @return Something aka date.
     */
    protected abstract T instance();
}
