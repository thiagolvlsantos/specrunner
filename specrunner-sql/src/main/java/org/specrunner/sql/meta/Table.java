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

public class Table {

    private Schema schema;
    private String alias;
    private String name;
    private Map<String, Column> aliasToColumns = new HashMap<String, Column>();
    private Map<String, Column> namesToColumns = new HashMap<String, Column>();

    public Schema getSchema() {
        return schema;
    }

    public Table setSchema(Schema schema) {
        this.schema = schema;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public Table setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getName() {
        return name;
    }

    public Table setName(String name) {
        this.name = name;
        return this;
    }

    public Table add(Column column) {
        aliasToColumns.put(column.getAlias(), column);
        namesToColumns.put(column.getName(), column);
        column.setTable(this);
        return this;
    }

    public Column getAlias(String alias) {
        return aliasToColumns.get(alias);
    }

    public Column getName(String name) {
        return namesToColumns.get(name);
    }

    public Map<String, Column> getAliasToColumns() {
        return aliasToColumns;
    }

    public void setAliasToColumns(Map<String, Column> aliasToColumns) {
        this.aliasToColumns = aliasToColumns;
    }

    public Map<String, Column> getNamesToColumns() {
        return namesToColumns;
    }

    public void setNamesToColumns(Map<String, Column> namesToColumns) {
        this.namesToColumns = namesToColumns;
    }

    public List<Column> getKeys() {
        List<Column> result = new LinkedList<Column>();
        for (Column c : namesToColumns.values()) {
            if (c.isKey()) {
                result.add(c);
            }
        }
        return result;
    }
}