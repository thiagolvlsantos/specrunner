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
package org.specrunner.report.core.comparators;

import java.util.Comparator;

import org.specrunner.report.core.Resume;

/**
 * Default report time comparator.
 * <p>
 * Ordered by: time (countdown), index.
 * 
 * @author Thiago Santos
 * 
 */
public final class TimeComparator implements Comparator<Resume> {

    /**
     * Thread safe instance.
     */
    private static final TimeComparator INSTANCE = new TimeComparator();

    /**
     * Default constructor.
     */
    private TimeComparator() {
    }

    /**
     * The instance access method.
     * 
     * @return The comparator.
     */
    public static TimeComparator get() {
        return INSTANCE;
    }

    @Override
    public int compare(Resume o1, Resume o2) {
        int result = (int) (o2.getTime() - o1.getTime());
        if (result == 0) {
            result = o1.getStatus().compareTo(o2.getStatus());
            if (result == 0) {
                result = o2.getStatusCounter() - o1.getStatusCounter();
                if (result == 0) {
                    result = o2.getAssertionCounter() - o1.getAssertionCounter();
                    if (result == 0) {
                        result = o1.getIndex() - o2.getIndex();
                    }
                }
            }
        }
        return result;
    }
}
