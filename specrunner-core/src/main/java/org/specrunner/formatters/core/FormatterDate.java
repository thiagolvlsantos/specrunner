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
package org.specrunner.formatters.core;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Formatter.
 * 
 * @author Thiago Santos
 */
@SuppressWarnings("serial")
public class FormatterDate extends AbstractFormatterTime<SimpleDateFormat> {

    @Override
    protected boolean testType(Object value) {
        return value instanceof Date || value instanceof Timestamp;
    }

    @Override
    protected SimpleDateFormat newInstance(String str) {
        return new SimpleDateFormat(str);
    }

    @Override
    protected String format(SimpleDateFormat pattern, Object value, Object[] args) {
        return pattern.format((Date) value);
    }
}
