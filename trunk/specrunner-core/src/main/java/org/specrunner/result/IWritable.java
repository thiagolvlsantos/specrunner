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
package org.specrunner.result;

import java.util.Map;

/**
 * Stands for a object with extra information.
 * 
 * @author Thiago Santos
 * 
 */
public interface IWritable {

    /**
     * If writable has information.
     * 
     * @return true, if has information (non null and not empty), false,
     *         otherwise.
     */
    boolean hasInformation();

    /**
     * Extra information mapping.
     * 
     * @return The information.
     */
    Map<String, Object> getInformation();

    /**
     * Write the object to target and returns a map from names to results.
     * 
     * @param target
     *            The target. i.e. a file to write to.
     * @return A reference to the final resource written. i.e. the final name of
     *         the target file.
     * @throws ResultException
     *             On writing errors.
     */
    Map<String, String> writeTo(String target) throws ResultException;
}