/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.readers;

import org.specrunner.context.IContext;
import org.specrunner.util.reset.IResetable;

/**
 * Stand for something which read a text from a object.
 * 
 * @author Thiago Santos
 *
 */
public interface IReader extends IResetable {

    /**
     * Read the content of an object as String.
     * 
     * @param context
     *            The test context.
     * @param obj
     *            An object reader.
     * @param args
     *            Arguments of reading.
     * @return A text.
     * @throws ReaderException
     *             On reading errors.
     */
    String read(IContext context, Object obj, Object[] args) throws ReaderException;

    /**
     * Says to replace content after reading.
     * 
     * @return true, if replace is enabled, false, otherwise.
     */
    boolean isReplacer();
}
