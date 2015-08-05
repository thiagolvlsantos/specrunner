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
package org.specrunner.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Given two result sets provides an enumeration of both. The default
 * enumeration provide both items (expected and received) when the primary keys.
 * 
 * @author Thiago Santos
 * 
 */
public interface IResultEnumerator {

    /**
     * Check if there are more elements.
     * 
     * @return true, if there are more elements.
     * @throws SQLException
     *             On errors.
     */
    boolean next() throws SQLException;

    /**
     * Get the reference result set.
     * 
     * @return null, if the corresponding reference value does not exist, the
     *         reference result set, otherwise.
     */
    ResultSet getExpected();

    /**
     * Get the system result set.
     * 
     * @return null, if the corresponding system value does not exist, the
     *         system result set, otherwise.
     */
    ResultSet getReceived();
}
