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

import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.comparer.impl.ComparatorDefault;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.impl.ConverterDefault;

public class Column {

    private Table table;
    private String alias;
    private String name;
    private boolean key;
    private static IConverter CONVERTER_DEFAULT = new ConverterDefault();
    private IConverter converter = CONVERTER_DEFAULT;
    private static IComparator COMPARATOR_DEFAULT = new ComparatorDefault();
    private IComparator comparator = COMPARATOR_DEFAULT;
    private Object defaultValue;

    public Table getTable() {
        return table;
    }

    public Column setTable(Table table) {
        this.table = table;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public Column setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getName() {
        return name;
    }

    public Column setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isKey() {
        return key;
    }

    public Column setKey(boolean key) {
        this.key = key;
        return this;
    }

    public IConverter getConverter() {
        return converter;
    }

    public Column setConverter(IConverter converter) {
        if (converter != null) {
            this.converter = converter;
        }
        return this;
    }

    public IComparator getComparator() {
        return comparator;
    }

    public Column setComparator(IComparator comparator) {
        if (comparator != null) {
            this.comparator = comparator;
        }
        return this;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Column setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}