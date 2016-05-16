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
package org.specrunner.junit;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory of threads named.
 * 
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * 
 */
public class NamedFactory implements ThreadFactory {
    /**
     * Pool number.
     */
    protected final AtomicInteger poolNumber = new AtomicInteger(1);
    /**
     * Thread number.
     */
    protected final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * Thread group.
     */
    protected final ThreadGroup group;

    /**
     * Friendly constructor.
     * 
     * @param poolName
     *            The pool name.
     */
    public NamedFactory(String poolName) {
        group = new ThreadGroup(poolName + "-" + poolNumber.getAndIncrement());
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(group, r, group.getName() + "-thread-" + threadNumber.getAndIncrement(), 0);
    }
}
