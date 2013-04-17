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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Schema meta-model.
 * 
 * @author Thiago Santos.
 * 
 */
public class Schema {

    /**
     * Schema alias.
     */
    private String alias;
    /**
     * Schema name.
     */
    private String name;
    /**
     * Mapping from alias to tables.
     */
    private Map<String, Table> aliasToTables = new HashMap<String, Table>();
    /**
     * Mapping from names to tables.
     */
    private Map<String, Table> namesToTables = new HashMap<String, Table>();

    /**
     * The schema alias.
     * 
     * @return The alias.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Set the alias.
     * 
     * @param alias
     *            The alias.
     * @return The schema itself.
     */
    public Schema setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    /**
     * The schema name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the schema name.
     * 
     * @param name
     *            The name.
     * @return The schema itself.
     */
    public Schema setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Add a table to the schema.
     * 
     * @param table
     *            A table.
     * @return The schema itself.
     */
    public Schema add(Table table) {
        aliasToTables.put(table.getAlias(), table);
        namesToTables.put(table.getName(), table);
        table.setSchema(this);
        return this;
    }

    /**
     * Get a table by its alias.
     * 
     * @param alias
     *            The alias.
     * @return A table, or null, if not found.
     */
    public Table getAlias(String alias) {
        return aliasToTables.get(alias);
    }

    /**
     * Get a table by its name.
     * 
     * @param name
     *            The name.
     * @return A table, or null, if not found.
     */
    public Table getName(String name) {
        return namesToTables.get(name);
    }

    /**
     * Gets the alias mapping.
     * 
     * @return The mapping.
     */
    public Map<String, Table> getAliasToTables() {
        return aliasToTables;
    }

    /**
     * Set the alias mapping.
     * 
     * @param aliasToTables
     *            The mapping.
     */
    public void setAliasToTables(Map<String, Table> aliasToTables) {
        this.aliasToTables = aliasToTables;
    }

    /**
     * Gets the name mapping.
     * 
     * @return The mapping.
     */
    public Map<String, Table> getNamesToTables() {
        return namesToTables;
    }

    /**
     * Sets the name mapping.
     * 
     * @param namesToTables
     *            The mapping.
     */
    public void setNamesToTables(Map<String, Table> namesToTables) {
        this.namesToTables = namesToTables;
    }

    /**
     * Get the column set.
     * 
     * @return The columns.
     */
    public Collection<Table> getTables() {
        return namesToTables.values();
    }
}