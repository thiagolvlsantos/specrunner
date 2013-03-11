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
package org.specrunner.sql.meta;

public class Value implements Comparable<Value> {

    private Column column;
    private Object value;

    public Value(Column column, Object value) {
        this.column = column;
        this.value = value;
    }

    public Column getColumn() {
        return column;
    }

    public Value setColumn(Column column) {
        this.column = column;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public Value setValue(Object value) {
        this.value = value;
        return this;
    }

    @Override
    public int compareTo(Value v) {
        return column.getName().compareTo(v.column.getName());
    }

    @Override
    public String toString() {
        return column.getName() + "=" + value;
    }
}