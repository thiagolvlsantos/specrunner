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

import org.specrunner.converters.ConverterException;

/**
 * Template for converter of enumerations. Allow creation of converters with
 * specific names without requiring to write arguments when used.
 * 
 * @author Thiago Santos.
 */
@SuppressWarnings("serial")
public abstract class ConverterEnumTemplate extends ConverterEnum {

    /**
     * Template parameters.
     */
    private Object[] template;

    /**
     * Conversion of specific types.
     * 
     * @param type
     *            The type.
     * @param lookupMethod
     *            The method to use on enumeration lookup.
     */
    public ConverterEnumTemplate(Class<? extends Enum<?>> type, String lookupMethod) {
        this.template = new Object[] { type, lookupMethod };
    }

    @Override
    public Object convert(Object obj, Object[] args) throws ConverterException {
        return super.convert(obj, template);
    }
}