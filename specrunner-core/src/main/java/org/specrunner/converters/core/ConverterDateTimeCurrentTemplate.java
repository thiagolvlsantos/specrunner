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

import java.util.List;

import org.joda.time.DateTime;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDateTimeCurrentTemplate extends AbstractConverterJodatimeCurrentTemplate<DateTime> {

    /**
     * See superclass.
     * 
     * @param values
     *            Value.
     */
    public ConverterDateTimeCurrentTemplate(List<String> values) {
        super(values);
    }

    @Override
    protected DateTime instance() {
        return new DateTime();
    }

    @Override
    protected Class<DateTime> type() {
        return DateTime.class;
    }

    @Override
    protected DateTime postProcess(Object value, Object[] args, DateTime result) {
        return UtilJodatime.postProcess(value, args, result, pattern, aliasToField, fieldToMethod);
    }
}
