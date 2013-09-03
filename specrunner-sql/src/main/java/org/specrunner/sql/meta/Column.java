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

import org.specrunner.SpecRunnerServices;
import org.specrunner.comparators.IComparator;
import org.specrunner.converters.IConverter;

/**
 * Column meta model.
 * 
 * @author Thiago Santos.
 * 
 */
public class Column {

    /**
     * Column table.
     */
    private Table table;
    /**
     * Column alias.
     */
    private String alias;
    /**
     * Column name.
     */
    private String name;
    /**
     * Column key indicator.
     */
    private boolean key;
    /**
     * Column default converter.
     */
    private static final IConverter CONVERTER_DEFAULT = SpecRunnerServices.getConverterManager().getDefault();
    /**
     * Column converter.
     */
    private IConverter converter = CONVERTER_DEFAULT;
    /**
     * Column default comparator.
     */
    private static final IComparator COMPARATOR_DEFAULT = SpecRunnerServices.getComparatorManager().getDefault();
    /**
     * Column comparator.
     */
    private IComparator comparator = COMPARATOR_DEFAULT;
    /**
     * Column default value.
     */
    private Object defaultValue;

    /**
     * Get the column table.
     * 
     * @return The table.
     */
    public Table getTable() {
        return table;
    }

    /**
     * Set the table.
     * 
     * @param table
     *            The table.
     * @return The column itself.
     */
    public Column setTable(Table table) {
        this.table = table;
        return this;
    }

    /**
     * Get the table alias.
     * 
     * @return The alias.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Set the table alias.
     * 
     * @param alias
     *            The alias.
     * @return The table itself.
     */
    public Column setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    /**
     * Get the table name.
     * 
     * @return The table.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the table name.
     * 
     * @param name
     *            The name.
     * @return The column itself.
     */
    public Column setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the key information.
     * 
     * @return true, if the column is a key, false, otherwise.
     */
    public boolean isKey() {
        return key;
    }

    /**
     * Set the key flag.
     * 
     * @param key
     *            The value.
     * @return The column itself.
     */
    public Column setKey(boolean key) {
        this.key = key;
        return this;
    }

    /**
     * Get the column converter.
     * 
     * @return The converter.
     */
    public IConverter getConverter() {
        return converter;
    }

    /**
     * Set the column converter.
     * 
     * @param converter
     *            The converter.
     * @return The column itself.
     */
    public Column setConverter(IConverter converter) {
        if (converter != null) {
            this.converter = converter;
        }
        return this;
    }

    /**
     * Get the column comparator.
     * 
     * @return The comparator.
     */
    public IComparator getComparator() {
        return comparator;
    }

    /**
     * Set the column comparator.
     * 
     * @param comparator
     *            The comparator.
     * @return The column itself.
     */
    public Column setComparator(IComparator comparator) {
        if (comparator != null) {
            this.comparator = comparator;
        }
        return this;
    }

    /**
     * Get the column default value. Default values are expected to be non null.
     * 
     * @return The default value.
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Set the column default value.
     * 
     * @param defaultValue
     *            The value.
     * @return The column itself.
     */
    public Column setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}