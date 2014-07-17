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

import org.specrunner.comparators.IComparator;
import org.specrunner.util.xom.CellAdapter;

/**
 * The value for a given cell in data tables.
 * 
 * @author Thiago Santos
 * 
 */
public class Value implements Comparable<Value> {

    /**
     * The reference column.
     */
    private Column column;
    /**
     * The cell adapter.
     */
    private CellAdapter cell;
    /**
     * The cell value.
     */
    private Object value;
    /**
     * The cell comparator.
     */
    private IComparator comparator;

    /**
     * Default constructor.
     * 
     * @param column
     *            The column.
     * @param cell
     *            The cell.
     * @param value
     *            The value.
     * @param comparator
     *            The comparator.
     */
    public Value(Column column, CellAdapter cell, Object value, IComparator comparator) {
        this.column = column;
        this.cell = cell;
        this.value = value;
        this.comparator = comparator;
    }

    /**
     * Get the value column.
     * 
     * @return The column.
     */
    public Column getColumn() {
        return column;
    }

    /**
     * Set the column.
     * 
     * @param column
     *            The column.
     * @return The value itself.
     */
    public Value setColumn(Column column) {
        this.column = column;
        return this;
    }

    /**
     * Get the source cell.
     * 
     * @return The source.
     */
    public CellAdapter getCell() {
        return cell;
    }

    /**
     * Set the cell.
     * 
     * @param cell
     *            A new cell.
     */
    public void setCell(CellAdapter cell) {
        this.cell = cell;
    }

    /**
     * Gets the value corresponding to the value.
     * 
     * @return The object.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Set the value.
     * 
     * @param value
     *            The new value.
     * @return The value object itself.
     */
    public Value setValue(Object value) {
        this.value = value;
        return this;
    }

    /**
     * Gest the value comparator.
     * 
     * @return The comparator.
     */
    public IComparator getComparator() {
        return comparator;
    }

    /**
     * Set the cell comparator.
     * 
     * @param comparator
     *            A comparator.
     * @return The value itself.
     */
    public Value setComparator(IComparator comparator) {
        this.comparator = comparator;
        return this;
    }

    @Override
    public int compareTo(Value v) {
        // sort names is required by prepared statements cache. Remember.
        return column.getName().compareTo(v.column.getName());
    }

    @Override
    public String toString() {
        return column.getName() + "=" + String.valueOf(value);
    }

}