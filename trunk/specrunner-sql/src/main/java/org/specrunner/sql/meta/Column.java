package org.specrunner.sql.meta;

public class Column {

    private Table table;
    private String alias;
    private String name;
    private boolean key;
    private IConverter converter = new ConverterDefault();
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

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Column setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}