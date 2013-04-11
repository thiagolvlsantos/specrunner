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
package org.specrunner.sql;

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
    INSERT("I"),
    /**
     * Stand for update operations.
     */
    UPDATE("U"),
    /**
     * Stand for delete operations.
     */
    DELETE("D");

    /**
     * The operation key.
     */
    private String key;

    /**
     * Default constructor.
     * 
     * @param key
     *            Element key.
     */
    private CommandType(String key) {
        this.key = key;
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
}
