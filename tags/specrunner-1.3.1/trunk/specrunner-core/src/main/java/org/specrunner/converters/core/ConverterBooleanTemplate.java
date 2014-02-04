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
package org.specrunner.converters.core;

import org.specrunner.converters.ConverterException;

/**
 * The boolean template converter.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterBooleanTemplate extends ConverterNotNullNotEmpty {

    /**
     * String for '<code>TRUE</code>'.
     */
    private String yes;
    /**
     * String for '<code>FALSE</code>'.
     */
    private String no;

    /**
     * Creates boolean converter using some strings for 'true'and 'false'.
     * 
     * @param yes
     *            The TRUE string.
     * @param no
     *            The FALSE string.
     */
    public ConverterBooleanTemplate(String yes, String no) {
        this.yes = yes.toLowerCase().trim();
        this.no = no.toLowerCase().trim();
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        String tmp = String.valueOf(value).toLowerCase().trim();
        if (yes.equals(tmp)) {
            return Boolean.TRUE;
        } else if (no.equals(tmp)) {
            return Boolean.FALSE;
        } else {
            throw new ConverterException("Value '" + tmp + "' not valid, use \n'" + yes + "' or '" + no + "'");
        }
    }
}
