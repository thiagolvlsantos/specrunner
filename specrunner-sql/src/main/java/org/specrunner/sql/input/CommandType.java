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
