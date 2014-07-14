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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Table meta model.
 * 
 * @author Thiago Santos
 * 
 */
public class Table implements IReplicable<Table>, IMergeable<Table> {

    /**
     * The parent schema.
     */
    private Schema schema;
    /**
     * The table alias.
     */
    private String alias;
    /**
     * The table name.
     */
    private String name;
    /**
     * List of column names.
     */
    private List<Column> columns = new LinkedList<Column>();
    /**
     * Map from alias to columns.
     */
    private Map<String, Column> aliasToColumns = new HashMap<String, Column>();
    /**
     * Map form names to columns.
     */
    private Map<String, Column> namesToColumns = new HashMap<String, Column>();

    /**
     * Get the schema.
     * 
     * @return The schema.
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * Set the schema.
     * 
     * @param schema
     *            The schema.
     * @return The table itself.
     */
    public Table setSchema(Schema schema) {
        this.schema = schema;
        return this;
    }

    /**
     * Get the alias.
     * 
     * @return The alias.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Set table alias.
     * 
     * @param alias
     *            The alias.
     * @return The table itself.
     */
    public Table setAlias(String alias) {
        this.alias = alias == null ? null : UtilNames.normalize(alias.toLowerCase());
        return this;
    }

    /**
     * Get the name.
     * 
     * @return The table name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     * 
     * @param name
     *            The name.
     * @return The table itself.
     */
    public Table setName(String name) {
        this.name = name == null ? null : name.toUpperCase();
        return this;
    }

    /**
     * Get the columns.
     * 
     * @return The columns.
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * Set the columns.
     * 
     * @param columns
     *            The columns.
     * @return The table itself.
     */
    public Table setColumns(List<Column> columns) {
        this.columns = columns;
        return this;
    }

    /**
     * Add a column.
     * 
     * @param column
     *            A column.
     * @return The table itself.
     */
    public Table add(Column column) {
        columns.add(column);
        aliasToColumns.put(column.getAlias(), column);
        namesToColumns.put(column.getName(), column);
        column.setTable(this);
        return this;
    }

    /**
     * Get a column by its alias.
     * 
     * @param alias
     *            The alias.
     * @return The column.
     */
    public Column getAlias(String alias) {
        return aliasToColumns.get(alias == null ? null : UtilNames.normalize(alias));
    }

    /**
     * Get the column by its name.
     * 
     * @param name
     *            The name.
     * @return The column name.
     */
    public Column getName(String name) {
        return namesToColumns.get(name == null ? null : name.toUpperCase());
    }

    /**
     * Get the alias mapping.
     * 
     * @return The mapping.
     */
    public Map<String, Column> getAliasToColumns() {
        return aliasToColumns;
    }

    /**
     * Set the alias mapping.
     * 
     * @param aliasToColumns
     *            A new mapping.
     */
    public void setAliasToColumns(Map<String, Column> aliasToColumns) {
        this.aliasToColumns = aliasToColumns;
    }

    /**
     * Get the name mapping.
     * 
     * @return The mapping
     */
    public Map<String, Column> getNamesToColumns() {
        return namesToColumns;
    }

    /**
     * Set the name mapping.
     * 
     * @param namesToColumns
     *            A new mapping.
     */
    public void setNamesToColumns(Map<String, Column> namesToColumns) {
        this.namesToColumns = namesToColumns;
    }

    /**
     * Get the columns which are keys.
     * 
     * @return The table keys.
     */
    public List<Column> getKeys() {
        List<Column> result = new LinkedList<Column>();
        for (Column c : columns) {
            if (c.isKey()) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * Get the columns which are references.
     * 
     * @return The table references.
     */
    public List<Column> getReferences() {
        List<Column> result = new LinkedList<Column>();
        for (Column c : columns) {
            if (c.isReference()) {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * Get the columns which are references.
     * 
     * @return The table references.
     */
    public List<Column> getVirtual() {
        List<Column> result = new LinkedList<Column>();
        for (Column c : columns) {
            if (c.isVirtual()) {
                result.add(c);
            }
        }
        return result;
    }

    @Override
    public Table copy() {
        Table copy = new Table().setSchema(schema).setName(name).setAlias(alias);
        for (Column c : columns) {
            copy.add(c.copy());
        }
        return copy;
    }

    @Override
    public void merge(Table other) {
        if (other == null) {
            return;
        }
        setName(other.name);
        setAlias(other.alias);
        for (Column c : other.getColumns()) {
            Column old = getName(c.getName());
            if (old == null) {
                add(c.copy());
            } else {
                old.merge(c);
            }
        }
    }
}