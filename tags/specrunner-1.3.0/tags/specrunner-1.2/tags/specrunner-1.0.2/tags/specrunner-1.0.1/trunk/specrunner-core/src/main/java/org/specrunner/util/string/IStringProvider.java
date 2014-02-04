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
package org.specrunner.util.string;

import org.specrunner.context.ContextException;
import org.specrunner.context.IContext;

/**
 * Given a context returns a String.
 * 
 * @author Thiago Santos
 * 
 */
public interface IStringProvider {

    /**
     * Provides a String based, or not, on the given context.
     * 
     * @param context
     *            A context.
     * @return A string.
     * @throws ContextException
     *             On evaluation errors.
     */
    String newString(IContext context) throws ContextException;
}