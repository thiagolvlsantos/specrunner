package org.specrunner.sql.meta;

import java.util.HashMap;
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
}