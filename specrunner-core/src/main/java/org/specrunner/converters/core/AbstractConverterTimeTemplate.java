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

import java.util.LinkedList;
import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.converters.ConverterException;
import org.specrunner.util.string.IStringNormalizer;

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
    protected List<String> values;

    /**
     * Constructor using strings.
     * 
     * @param values
     *            The values to be converted to date.
     */
    public AbstractConverterTimeTemplate(List<String> values) {
        if (values == null) {
            this.values = null;
        } else {
            this.values = new LinkedList<String>();
            IStringNormalizer sn = SRServices.get(IStringNormalizer.class);
            for (String s : values) {
                this.values.add(sn.camelCase(s));
            }
        }
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        T result = null;
        String str = SRServices.get(IStringNormalizer.class).camelCase(String.valueOf(value));
        if (values != null) {
            for (String v : values) {
                if (testValue(str, v)) {
                    result = instance();
                    break;
                }
            }
        }
        if (result == null) {
            throw new ConverterException("Invalid value '" + value + "'.");
        } else {
            result = postProcess(value, args, result);
        }
        return result;
    }

    /**
     * Test the string against a value.
     * 
     * @param str
     *            String.
     * @param value
     *            A value.
     * @return true, if valid, false, otherwise.
     */
    protected boolean testValue(String str, String value) {
        return str.contains(value);
    }

    /**
     * Creates an instance of date.
     * 
     * @return Something aka date.
     */
    protected abstract T instance();

    /**
     * Post process a instance of data.
     * 
     * @param value
     *            The original value.
     * @param args
     *            The arguments.
     * @param result
     *            The result.
     * @return A time processed. i.e. plusDays, if specified.
     */
    protected T postProcess(Object value, Object[] args, T result) {
        return result;
    }
}
