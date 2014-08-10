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
package org.specrunner.sql.meta.impl;

import org.specrunner.context.IContext;
import org.specrunner.parameters.core.ParameterHolder;
import org.specrunner.plugins.PluginException;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.IDataFilter;
import org.specrunner.sql.meta.IRegister;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;

/**
 * Encapsulate a filter and negate it answers.
 * 
 * @author Thiago Santos
 * 
 */
public class DataFilterNegative extends ParameterHolder implements IDataFilter {

    /**
     * Reference filter.
     */
    private IDataFilter dataFilter;

    /**
     * The reference filter.
     * 
     * @param dataFilter
     *            The filter.
     */
    public DataFilterNegative(IDataFilter dataFilter) {
        this.dataFilter = dataFilter;
    }

    @Override
    public void setup(Schema schema, IContext context) throws PluginException {
        dataFilter.setup(schema, context);
    }

    @Override
    public boolean accept(Schema schema) {
        return !dataFilter.accept(schema);
    }

    @Override
    public boolean accept(Table table) {
        return !dataFilter.accept(table);
    }

    @Override
    public boolean accept(IRegister register) {
        return !dataFilter.accept(register);
    }

    @Override
    public boolean accept(Column column) {
        return !dataFilter.accept(column);
    }

    @Override
    public boolean accept(Column column, Object value) {
        return !dataFilter.accept(column, value);
    }
}