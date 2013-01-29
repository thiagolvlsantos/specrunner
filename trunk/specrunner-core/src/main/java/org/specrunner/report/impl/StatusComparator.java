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
package org.specrunner.report.impl;

import java.util.Comparator;

/**
 * Default report status comparator.
 * <p>
 * Ordered by fields: status (most relevante first), status counter (countdown),
 * assertion count(countdown), time(countdown), index.
 * 
 * @author Thiago Santos
 * 
 */
public final class StatusComparator implements Comparator<Resume> {
    /**
     * Unique instance.
     */
    private static final StatusComparator INSTANCE = new StatusComparator();

    /**
     * Default constructor.
     */
    private StatusComparator() {
    }

    /**
     * The instance access method.
     * 
     * @return The comparator.
     */
    public static StatusComparator get() {
        return INSTANCE;
    }

    @Override
    public int compare(Resume o1, Resume o2) {
        int result = o1.getStatus().compareTo(o2.getStatus());
        if (result == 0) {
            result = o2.getStatusCounter() - o1.getStatusCounter();
            if (result == 0) {
                result = o2.getAssertionCounter() - o1.getAssertionCounter();
                if (result == 0) {
                    result = (int) (o2.getTime() - o1.getTime());
                    if (result == 0) {
                        result = o1.getIndex() - o2.getIndex();
                    }
                }
            }
        }
        return result;
    }
}