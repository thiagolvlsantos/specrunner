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

import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;

/**
 * Default report filter. Filter nothing. :)
 * 
 * @author Thiago Santos
 * 
 */
public class FilterDefault implements IFilter {

    @Override
    public boolean accept(Schema schema) {
        return true;
    }

    @Override
    public boolean accept(Schema schema, Table table) {
        return true;
    }

    @Override
    public boolean accept(Schema schema, Table table, Column column) {
        return true;
    }

    @Override
    public boolean accept(Schema schema, Table table, Column column, Object value) {
        return true;
    }
}