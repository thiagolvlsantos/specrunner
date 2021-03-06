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

import org.specrunner.converters.ConverterException;

/**
 * Template for converter of enumerations. Allow creation of converters with
 * specific names without requiring to write arguments when used.
 * 
 * @author Thiago Santos.
 */
@SuppressWarnings("serial")
public abstract class ConverterEnumValueTemplate extends ConverterEnumValue {

    /**
     * Template parameters.
     */
    protected Object[] template;

    /**
     * Conversion of specific types.
     * 
     * @param type
     *            The type.
     * @param lookupMethod
     *            The method to use on enumeration lookup.
     * @param outputMethod
     *            The method to use on enumeration output.
     */
    public ConverterEnumValueTemplate(Class<? extends Enum<?>> type, String lookupMethod, String outputMethod) {
        this.template = new Object[] { type, lookupMethod, outputMethod };
    }

    @Override
    public Object convert(Object obj, Object[] args) throws ConverterException {
        return super.convert(obj, template);
    }

    @Override
    public Object revert(Object obj, Object[] args) throws ConverterException {
        return super.revert(obj, template);
    }
}
