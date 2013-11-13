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
import org.specrunner.plugins.PluginException;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;

/**
 * Encapsulate a filter and negate it answers.
 * 
 * @author Thiago Santos
 * 
 */
public class FilterNegative implements IFilter {

    /**
     * Reference filter.
     */
    private IFilter filter;

    /**
     * The reference filter.
     * 
     * @param filter
     *            The filter.
     */
    public FilterNegative(IFilter filter) {
        this.filter = filter;
    }

    @Override
    public void setup(Schema schema, IContext context) throws PluginException {
        filter.setup(schema, context);
    }

    @Override
    public boolean accept(Schema schema) {
        return !filter.accept(schema);
    }

    @Override
    public boolean accept(Table table) {
        return !filter.accept(table);
    }

    @Override
    public boolean accept(Column column) {
        return !filter.accept(column);
    }

    @Override
    public boolean accept(Column column, Object value) {
        return !filter.accept(column, value);
    }
}