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

import java.util.List;

import org.joda.time.MutableDateTime;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterMutableDateTimeCurrentTemplate extends AbstractConverterJodatimeCurrentTemplate<MutableDateTime> {

    /**
     * See superclass.
     * 
     * @param values
     *            Value.
     */
    public ConverterMutableDateTimeCurrentTemplate(List<String> values) {
        super(values);
    }

    @Override
    protected MutableDateTime instance() {
        return new MutableDateTime();
    }

    @Override
    protected Class<MutableDateTime> type() {
        return MutableDateTime.class;
    }

    @Override
    protected MutableDateTime postProcess(Object value, Object[] args, MutableDateTime result) {
        return UtilJodatime.postProcess(value, args, result, pattern, aliasToField, fieldToMethod);
    }
}
