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
package org.specrunner.comparators.core;

import org.specrunner.util.UtilString;

/**
 * Compare two strings using <code>UtilString.normalized(...)</code> to clean
 * strings.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ComparatorStringNormalized extends ComparatorString {

    @Override
    protected String toString(Object obj) {
        return UtilString.normalize(super.toString(obj));
    }
}