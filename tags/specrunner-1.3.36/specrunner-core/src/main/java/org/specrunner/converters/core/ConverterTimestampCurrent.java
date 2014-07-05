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

/**
 * Create current date (in timestamp).
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterTimestampCurrent extends ConverterTimestampCurrentTemplate {

    /**
     * Basic timestamp converter.
     */
    public ConverterTimestampCurrent() {
        super(new String[] { "hora atual", "data hora atual", "current time", "current timestamp" });
    }
}