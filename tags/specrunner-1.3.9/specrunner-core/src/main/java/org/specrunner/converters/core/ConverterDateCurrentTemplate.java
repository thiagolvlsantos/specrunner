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

import java.util.Date;

import org.specrunner.converters.ConverterException;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDateCurrentTemplate extends AbstractConverterTimeTemplate<Date> {

    /**
     * See superclass.
     * 
     * @param regexp
     *            Regexp.
     */
    public ConverterDateCurrentTemplate(String regexp) {
        super(regexp);
    }

    /**
     * See superclass.
     * 
     * @param values
     *            Value.
     */
    public ConverterDateCurrentTemplate(String[] values) {
        super(values);
    }

    @Override
    protected Date instance() {
        return new Date();
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value instanceof Date) {
            return value;
        }
        return super.convert(value, args);
    }
}
