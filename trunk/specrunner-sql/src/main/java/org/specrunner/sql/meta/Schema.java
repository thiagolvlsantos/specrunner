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