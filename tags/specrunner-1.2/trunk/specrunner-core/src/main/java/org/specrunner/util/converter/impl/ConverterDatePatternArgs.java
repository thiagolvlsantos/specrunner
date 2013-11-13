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
package org.specrunner.util.converter.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.specrunner.util.converter.ConverterException;

/**
 * Convert any date (Date from Java), given a provided pattern in arg[0].
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDatePatternArgs extends ConverterDefault {

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(String.valueOf(args[0])).parse(String.valueOf(value));
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }
}