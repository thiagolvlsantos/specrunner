/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
import java.util.Map;

public class Schema {

    private String alias;
    private String name;
    private Map<String, Table> aliasToTables = new HashMap<String, Table>();
    private Map<String, Table> namesToTables = new HashMap<String, Table>();

    public String getAlias() {
        return alias;
    }

    public Schema setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getName() {
        return name;
    }

    public Schema setName(String name) {
        this.name = name;
        return this;
    }

    public Schema add(Table table) {
        aliasToTables.put(table.getAlias(), table);
        namesToTables.put(table.getName(), table);
        table.setSchema(this);
        return this;
    }

    public Table getAlias(String alias) {
        return aliasToTables.get(alias);
    }

    public Table getName(String name) {
        return namesToTables.get(name);
    }

    public Map<String, Table> getAliasToTables() {
        return aliasToTables;
    }

    public void setAliasToTables(Map<String, Table> aliasToTables) {
        this.aliasToTables = aliasToTables;
    }

    public Map<String, Table> getNamesToTables() {
        return namesToTables;
    }

    public void setNamesToTables(Map<String, Table> namesToTables) {
        this.namesToTables = namesToTables;
    }
}