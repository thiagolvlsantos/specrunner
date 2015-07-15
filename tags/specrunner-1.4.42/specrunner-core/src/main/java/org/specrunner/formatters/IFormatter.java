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
package org.specrunner.formatters;

import org.specrunner.util.reset.IResetable;

/**
 * A generic formatter interface.
 * 
 * @author Thiago Santos
 * 
 */
public interface IFormatter extends IResetable {

    /**
     * Format a given object to another using some arguments as auxiliary.
     * 
     * @param value
     *            The value to be formatted.
     * @param args
     *            The arguments.
     * @return The formatted object.
     * @throws FormatterException
     *             On format errors.
     */
    String format(Object value, Object[] args) throws FormatterException;
}
