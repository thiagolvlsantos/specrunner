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
package org.specrunner.formatters.core;

import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;

/**
 * Formatter.
 * 
 * @author Thiago Santos
 */
@SuppressWarnings("serial")
public class FormatterJodatime extends AbstractFormatterTime<DateTimeFormatter> {

    @Override
    protected boolean testType(Object value) {
        return value instanceof ReadableInstant || value instanceof ReadablePartial;
    }

    @Override
    protected DateTimeFormatter newInstance(String str) {
        return null;
    }

    @Override
    protected String format(DateTimeFormatter pattern, Object value, Object[] args) {
        return (new DateTime(value)).toString(String.valueOf(args[0]));
    }
}