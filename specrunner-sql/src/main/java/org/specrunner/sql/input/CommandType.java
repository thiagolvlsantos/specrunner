package org.specrunner.sql.input;

public enum CommandType {

    INSERT("I"), UPDATE("U"), DELETE("D");

    private String key;

    private CommandType(String key) {
        this.key = key;
    }

    public static CommandType get(String key) {
        for (CommandType c : values()) {
            if (c.key.equalsIgnoreCase(key)) {
                return c;
            }
        }
        return null;
    }
}
