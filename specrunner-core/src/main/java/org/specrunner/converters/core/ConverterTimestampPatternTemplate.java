/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
import java.util.Date;

import org.specrunner.converters.ConverterException;

/**
 * General timestamp pattern converter.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterTimestampPatternTemplate extends ConverterDatePatternTemplate {

    /**
     * Create a SimpleDateFormat using the given pattern.
     * 
     * @param pattern
     *            Pattern.
     */
    public ConverterTimestampPatternTemplate(String pattern) {
        super(pattern);
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        Object tmp = super.convert(value, args);
        if (tmp instanceof Date) {
            tmp = new Timestamp(((Date) tmp).getTime());
        }
        return tmp;
    }
}
