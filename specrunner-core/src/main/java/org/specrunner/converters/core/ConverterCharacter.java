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
 * Basic char converter.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ConverterCharacter extends ConverterNotNullNotEmpty {
    @Override
    public Object convert(Object obj, Object[] args) throws ConverterException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Character) {
            return obj;
        }
        try {
            String str = String.valueOf(obj);
            if (str.isEmpty()) {
                throw new ConverterException("Converter '" + getClass() + "' must have at least one character.");
            }
            return Character.valueOf(str.charAt(0));
        } catch (NumberFormatException e) {
            throw new ConverterException(e);
        }
    }
}
