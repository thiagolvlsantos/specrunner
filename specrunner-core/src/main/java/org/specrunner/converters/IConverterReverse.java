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
package org.specrunner.converters;

/**
 * A generic revert converter.
 * 
 * @author Thiago Santos
 * 
 */
public interface IConverterReverse extends IConverter {

    /**
     * Revert a given object to another using some arguments as auxiliary.
     * 
     * @param value
     *            The value to be reverted.
     * @param args
     *            The arguments.
     * @return The reversible object.
     * @throws ConverterException
     *             On conversion errors.
     */
    Object revert(Object value, Object[] args) throws ConverterException;
}
