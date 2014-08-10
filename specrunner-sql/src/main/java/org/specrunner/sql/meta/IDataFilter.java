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
package org.specrunner.sql.meta;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;

/**
 * Provides a multi-level database comparison filter.
 * 
 * @author Thiago Santos
 * 
 */
public interface IDataFilter {

    /**
     * CSS for filtered tables.
     */
    String CSS_SCHEMA = "sqlschemaignore";

    /**
     * CSS for filtered tables.
     */
    String CSS_TABLE = "sqltableignore";

    /**
     * CSS for filtered register.
     */
    String CSS_REGISTER = "sqlregisterignore";

    /**
     * CSS for filtered columns.
     */
    String CSS_COLUMN = "sqlcolumnignore";

    /**
     * CSS for filtered values.
     */
    String CSS_VALUE = "sqlvalueignore";

    /**
     * Enable the filter to perform some setup before use.
     * 
     * @param schema
     *            The schema under analysis.
     * @param context
     *            The context.
     * @throws PluginException
     *             On setup errors.
     */
    void setup(Schema schema, IContext context) throws PluginException;

    /**
     * Check if a schema is accepted.
     * 
     * @param schema
     *            A schema.
     * @return true, to compare, false, to ignore item.
     */
    boolean accept(Schema schema);

    /**
     * Check if a table in a schema is accepted.
     * 
     * @param table
     *            A table.
     * @return true, to compare, false, to ignore item.
     */
    boolean accept(Table table);

    /**
     * Check if a register in a schema is accepted.
     * 
     * @param register
     *            A register.
     * @return true, to compare, false, to ignore item.
     */
    boolean accept(IRegister register);

    /**
     * Check if a column, in table from a schema is accepted.
     * 
     * @param column
     *            A column.
     * @return true, to compare, false, to ignore item.
     */
    boolean accept(Column column);

    /**
     * Check if a value, in a column of a table from a schema is accepted.
     * 
     * @param column
     *            A column.
     * @param value
     *            A value.
     * @return true, to compare, false, to ignore item.
     */
    boolean accept(Column column, Object value);
}