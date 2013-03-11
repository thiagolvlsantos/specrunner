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
package org.specrunner.util.converter;

import org.specrunner.util.mapping.IResetable;

/**
 * A generic converter interface.
 * 
 * @author Thiago Santos
 * 
 */
public interface IConverter extends IResetable {

    /**
     * Convert a given object to another using some arguments as auxiliary.
     * 
     * @param value
     *            The value to be converted.
     * @param args
     *            The arguments.
     * @return The converted object.
     * @throws ConverterException
     *             On conversion errors.
     */
    Object convert(Object value, Object[] args) throws ConverterException;
}
