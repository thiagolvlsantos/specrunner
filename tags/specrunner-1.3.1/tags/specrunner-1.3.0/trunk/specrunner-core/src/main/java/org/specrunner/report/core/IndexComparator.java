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
package org.specrunner.report.core;

import java.util.Comparator;

/**
 * Default report status comparator.
 * <p>
 * Ordered by fields: index.
 * 
 * @author Thiago Santos
 * 
 */
public final class IndexComparator implements Comparator<Resume> {

    /**
     * Unique instance.
     */
    private static final IndexComparator INSTANCE = new IndexComparator();

    /**
     * Default constructor.
     */
    private IndexComparator() {
    }

    /**
     * The instance access method.
     * 
     * @return The comparator.
     */
    public static IndexComparator get() {
        return INSTANCE;
    }

    @Override
    public int compare(Resume o1, Resume o2) {
        return o1.getIndex() - o2.getIndex();
    }
}