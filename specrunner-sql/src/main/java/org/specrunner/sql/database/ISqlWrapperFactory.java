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
package org.specrunner.sql.database;

import org.specrunner.sql.meta.IRegister;
import org.specrunner.sql.meta.Table;

/**
 * A factory of SQL wrappers.
 * 
 * @author Thiago Santos.
 */
public interface ISqlWrapperFactory {

    /**
     * Create wrapper for inputs.
     * 
     * @param table
     *            A table.
     * @param command
     *            Command type.
     * @param register
     *            Set of values.
     * @param expectedCount
     *            Expected counter.
     * @return A wrapper.
     */
    SqlWrapper createInputWrapper(Table table, CommandType command, IRegister register, int expectedCount);

    /**
     * Create wrapper for outputs.
     * 
     * @param table
     *            A table.
     * @param command
     *            Command type.
     * @param register
     *            Set of values.
     * @param expectedCount
     *            Expected counter.
     * @return A wrapper.
     */
    SqlWrapper createOutputWrapper(Table table, CommandType command, IRegister register, int expectedCount);
}
