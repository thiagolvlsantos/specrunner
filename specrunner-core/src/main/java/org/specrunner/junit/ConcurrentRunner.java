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

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public class ConcurrentRunner extends BlockJUnit4ClassRunner {

    /**
     * Creates a concurrent runner.
     * 
     * @param klass
     *            The class.
     * @throws InitializationError
     *             On initialization error.
     */
    public ConcurrentRunner(final Class<?> klass) throws InitializationError {
        super(klass);
        setScheduler(new ConcurrentRunnerScheduler(klass));
    }
}
