/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.util.comparer.impl;

import org.specrunner.util.comparer.IComparator;

/**
 * Useful comparator to ignore a given cell or row.
 * 
 * @author Thiago Santos
 * 
 */
public class ComparatorTrue implements IComparator {

    @Override
    public Class<?> getType() {
        return Object.class;
    }

    @Override
    public void initialize() {
    }

    @Override
    public boolean equals(Object expected, Object received) {
        return true;
    }
}