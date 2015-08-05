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
package org.specrunner.sql.meta.impl;

import org.specrunner.context.IContext;
import org.specrunner.expressions.EMode;
import org.specrunner.parameters.core.ParameterHolder;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.IDataFilter;
import org.specrunner.sql.meta.IRegister;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;

/**
 * Default report filter. Filter nothing. :)
 * 
 * @author Thiago Santos
 * 
 */
public class DataFilterDefault extends ParameterHolder implements IDataFilter {

    @Override
    public void setup(IContext context, EMode mode, Schema schema) {
        // nothing.
    }

    @Override
    public boolean accept(EMode mode, Schema schema) {
        return true;
    }

    @Override
    public boolean accept(EMode mode, Table table) {
        return true;
    }

    @Override
    public boolean accept(EMode mode, IRegister register) {
        return true;
    }

    @Override
    public boolean accept(EMode mode, Column column) {
        return true;
    }

    @Override
    public boolean accept(EMode mode, Column column, Object value) {
        return true;
    }
}
