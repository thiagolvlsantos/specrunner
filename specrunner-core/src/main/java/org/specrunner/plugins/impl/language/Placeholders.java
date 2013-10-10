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
package org.specrunner.plugins.impl.language;

import java.util.HashMap;

/**
 * Common regular expressions. <code>Placeholders.get().put(name,regexp)</code>
 * to bind more types.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public final class Placeholders extends HashMap<String, String> {

    /**
     * Patterns for Regexp replacement.
     */
    private static final Placeholders INSTANCE = new Placeholders();

    /**
     * Default constructor.
     */
    private Placeholders() {
        put("$boolean", "('true'|'false')");

        put("$char", "(.)");

        put("$byte", "(\\d+)");
        put("$short", "(\\d+)");
        put("$int", "(\\d+)");
        put("$long", "(\\d+)");
        put("$number", "(\\d+)");

        put("$float", "(\\d+\\.?\\d?)");
        put("$double", "(\\d+\\.?\\d?)");

        put("$string", "(.+)");
        put("$quote", "\"(.+)\"");
    }

    /**
     * Get the patterns instance.
     * 
     * @return The instance.
     */
    public static Placeholders get() {
        return INSTANCE;
    }
}