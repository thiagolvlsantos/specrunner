/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package org.specrunner.sql.report;

import org.specrunner.context.IContext;
import org.specrunner.parameters.IParameterHolder;
import org.specrunner.plugins.PluginException;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;

/**
 * Provides a multi-level database comparison filter.
 * 
 * @author Thiago Santos
 * 
 */
public interface IFilter extends IParameterHolder {

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
     *            The schema.
     * @return true, to compare, false, to ignore item.
     */
    boolean accept(Schema schema);

    /**
     * Check if a table in a schema is accepted.
     * 
     * @param table
     *            The table.
     * @return true, to compare, false, to ignore item.
     */
    boolean accept(Table table);

    /**
     * Check if a column, in table from a schema is accepted.
     * 
     * @param column
     *            The column.
     * @return true, to compare, false, to ignore item.
     */
    boolean accept(Column column);

    /**
     * Check if a value, in a column of a table from a schema is accepted.
     * 
     * @param column
     *            The column.
     * @param value
     *            The value.
     * @return true, to compare, false, to ignore item.
     */
    boolean accept(Column column, Object value);
}