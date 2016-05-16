/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.sql.database;


/**
 * Abstraction for database command types.
 * 
 * @author Thiago Santos
 * 
 */
public enum CommandType {

    /**
     * Stand for insert operations.
     */
    INSERT("I", "Inserts"),
    /**
     * Stand for update operations.
     */
    UPDATE("U", "Updates"),
    /**
     * Stand for delete operations.
     */
    DELETE("D", "Deletes");

    /**
     * The operation key.
     */
    private String key;

    /**
     * A description.
     */
    private String description;

    /**
     * Default constructor.
     * 
     * @param key
     *            Element key.
     * @param description
     *            A short description of command type.
     */
    private CommandType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Lookup method.
     * 
     * @param key
     *            The key.
     * @return The command type, or null, if not found.
     */
    public static CommandType get(String key) {
        for (CommandType c : values()) {
            if (c.key.equalsIgnoreCase(key)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("'%s' for %s", key, description);
    }
}
