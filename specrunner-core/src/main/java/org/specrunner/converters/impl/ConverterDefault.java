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
package org.specrunner.converters.impl;

import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;

/**
 * Default converter. Bypass converter accepts everything.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ConverterDefault implements IConverter {

    @Override
    public void initialize() {
    }

    @Override
    public boolean accept(Object obj) {
        return true;
    }

    @Override
    public Object convert(Object obj, Object[] args) throws ConverterException {
        return obj;
    }
}