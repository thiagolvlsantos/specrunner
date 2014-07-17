/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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

import org.specrunner.sql.meta.Column;

/**
 * Abstraction for SQL column reader.
 * 
 * @author Thiago Santos.
 * 
 */
public interface IColumnReader {

    /**
     * Read a column from a result set.
     * 
     * @param rs
     *            A result set.
     * @param column
     *            A column to be read.
     * @return The object for that column.
     * @throws SQLException
     *             On read errors.
     */
    Object read(ResultSet rs, Column column) throws SQLException;
}
