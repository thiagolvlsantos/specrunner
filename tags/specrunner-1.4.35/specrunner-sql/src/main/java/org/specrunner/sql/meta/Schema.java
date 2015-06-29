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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.specrunner.context.IContext;
import org.specrunner.util.reset.IResetableExtended;

/**
 * Schema meta-model.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class Schema implements IReplicable<Schema>, IMergeable<Schema>, IResetableExtended {

    /**
     * Schema alias.
     */
    private String alias;
    /**
     * Schema name.
     */
    private String name;
    /**
     * Schema tables.
     */
    private List<Table> tables = new LinkedList<Table>();
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
        this.alias = alias == null ? null : UtilNames.normalize(alias);
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
        this.name = name == null ? null : name.toUpperCase();
        return this;
    }

    /**
     * Get the column set.
     * 
     * @return The columns.
     */
    public List<Table> getTables() {
        return tables;
    }

    /**
     * Set the tables.
     * 
     * @param tables
     *            The tables.
     * @return The schema itself.
     */
    public Schema setTables(List<Table> tables) {
        this.tables = tables;
        return this;
    }

    /**
     * Add a table to the schema.
     * 
     * @param table
     *            A table.
     * @return The schema itself.
     * @throws SchemaException
     *             On repetition errors.
     */
    public Schema add(Table table) throws SchemaException {
        if (namesToTables.containsKey(table.getName())) {
            throw new SchemaException("Table with name '" + table.getName() + "' already exist in schema (" + name + "," + alias + "), read before error: " + namesToTables.keySet());
        }
        if (aliasToTables.containsKey(table.getAlias())) {
            throw new SchemaException("Table with alias '" + table.getAlias() + "' already exist in schema (" + name + "," + alias + "), read before error: " + aliasToTables.keySet());
        }
        tables.add(table);
        aliasToTables.put(table.getAlias(), table);
        namesToTables.put(table.getName(), table);
        table.setParent(this);
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
        String key = alias == null ? null : UtilNames.normalize(alias);
        Table table = aliasToTables.get(key);
        if (table == null) {
            table = getName(key);
        }
        return table;
    }

    /**
     * Get a table by its name.
     * 
     * @param name
     *            The name.
     * @return A table, or null, if not found.
     */
    public Table getName(String name) {
        return namesToTables.get(name == null ? null : name.toUpperCase());
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

    @Override
    public Schema copy() throws ReplicableException {
        Schema copy = new Schema().setName(name).setAlias(alias);
        for (Table t : tables) {
            try {
                copy.add(t.copy());
            } catch (SchemaException e) {
                throw new ReplicableException(e);
            }
        }
        return copy;
    }

    @Override
    public void merge(Schema other) throws MergeableException {
        if (other == null) {
            return;
        }
        setName(other.name);
        setAlias(other.alias);
        for (Table t : other.getTables()) {
            Table old = getName(t.getName());
            if (old == null) {
                try {
                    add(t.copy());
                } catch (SchemaException e) {
                    throw new MergeableException(e);
                } catch (ReplicableException e) {
                    throw new MergeableException(e);
                }
            } else {
                old.merge(t);
            }
        }
    }

    @Override
    public void initialize(IContext context) {
        for (Table t : tables) {
            t.initialize(context);
        }
    }
}