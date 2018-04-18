/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.sql.meta;

import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.sql.database.DatabaseException;

/**
 * A register is a set of values. This is a semantic interface.
 * 
 * @author Thiago Santos
 */
public interface IRegister extends List<Value> {

    /**
     * Parent table.
     * 
     * @return A table.
     */
    Table getParent();

    /**
     * Get the table which is referred by the column.
     * 
     * @param context
     *            The test context.
     * @param column
     *            Base column.
     * @return The table or alias.
     * @throws DatabaseException
     *             On table resolution error.
     */
    String getTableOrAlias(IContext context, Column column) throws DatabaseException;

    /**
     * Get a value by its name.
     * 
     * @param name
     *            A name.
     * @return A value, if it exists, null, otherwise.
     */
    Value getByName(String name);

    /**
     * Get a value by its alias.
     * 
     * @param alias
     *            An alias.
     * @return A value, if it exists, null, otherwise.
     */
    Value getByAlias(String alias);

    /**
     * Check if register has keys.
     * 
     * @return true, if register has keys, false, otherwise.
     */
    boolean hasKeys();

    /**
     * Check if register has references.
     * 
     * @return true, if register has references, false, otherwise.
     */
    boolean hasReferences();

    /**
     * Check if register has keys or references.
     * 
     * @return true, if register has keys or references, false, otherwise.
     */
    boolean hasKeysOrReferences();
}
