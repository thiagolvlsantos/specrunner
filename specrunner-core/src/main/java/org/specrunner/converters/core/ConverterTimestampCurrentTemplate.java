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
import java.util.List;

/**
 * Create current date (in timestamp).
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterTimestampCurrentTemplate extends AbstractConverterJvmTimeCurrentTemplate<Timestamp> {

    /**
     * See superclass.
     * 
     * @param values
     *            Value.
     */
    public ConverterTimestampCurrentTemplate(List<String> values) {
        super(values);
    }

    @Override
    protected Timestamp instance() {
        return new Timestamp(getCalendar().getTimeInMillis());
    }

    @Override
    protected Class<Timestamp> type() {
        return Timestamp.class;
    }

    @Override
    protected Timestamp postProcess(Object value, Object[] args, Timestamp result) {
        return UtilDate.postProcess(value, args, getCalendar(), result, pattern, aliasToField, fieldToMethod);
    }
}
