package org.specrunner.sql.meta;

public class Value implements Comparable<Value> {

    private Column column;
    private Object value;

    public Value(Column column, Object value) {
        this.column = column;
        this.value = value;
    }

    public Column getColumn() {
        return column;
    }

    public Value setColumn(Column column) {
        this.column = column;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public Value setValue(Object value) {
        this.value = value;
        return this;
    }

    public int compareTo(Value v) {
        return column.getName().compareTo(v.column.getName());
    }

    @Override
    public String toString() {
        return column.getName() + "=" + value;
    }
}