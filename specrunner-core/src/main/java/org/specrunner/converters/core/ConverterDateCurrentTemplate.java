/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
import java.util.List;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDateCurrentTemplate extends AbstractConverterJvmTimeCurrentTemplate<Date> {

    /**
     * See superclass.
     * 
     * @param values
     *            Value.
     */
    public ConverterDateCurrentTemplate(List<String> values) {
        super(values);
    }

    @Override
    protected Date instance() {
        return getCalendar().getTime();
    }

    @Override
    protected Class<Date> type() {
        return Date.class;
    }

    @Override
    protected Date postProcess(Object value, Object[] args, Date result) {
        return UtilDate.postProcess(value, args, getCalendar(), result, pattern, aliasToField, fieldToMethod);
    }
}
