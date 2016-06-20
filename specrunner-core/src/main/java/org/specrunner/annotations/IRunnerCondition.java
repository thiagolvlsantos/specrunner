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
package org.specrunner.annotations;

import java.io.File;

public interface IRunnerCondition {

    /**
     * Perform a test condition verification, return true to skip test, false,
     * otherwise.
     * 
     * @param javaClass
     *            Fixture type, if exist, null, otherwise.
     * @param input
     *            Input resource, if exists.
     * @param runner
     *            Output resource, if exists.
     * 
     * @return true, to execute, false, otherwise.
     */
    boolean skip(Class<?> javaClass, File input, File output);
}