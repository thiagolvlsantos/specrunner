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
package org.specrunner.plugins;

/**
 * Factory types.
 * 
 * @author Thiago Santos
 */
public final class PluginKind {

    /**
     * Attribute based factories.
     */
    public static final PluginKind ATTRIBUTE = new PluginKind("attribute");
    /**
     * Style based factories.
     */
    public static final PluginKind CSS = new PluginKind("css");
    /**
     * Custom tags based factories.
     */
    public static final PluginKind CUSTOM = new PluginKind("custom");
    /**
     * Element based factories.
     */
    public static final PluginKind ELEMENT = new PluginKind("element");
    /**
     * Text factories.
     */
    public static final PluginKind TEXT = new PluginKind("text");

    /**
     * Kind of factory.
     */
    private String kind;

    /**
     * Default constructor.
     * 
     * @param kind
     *            The factory kind.
     */
    private PluginKind(String kind) {
        this.kind = kind;
    }

    /**
     * Create a new kind of factory.
     * 
     * @param kind
     *            The kind.
     * @return A plugin kind.
     */
    public static PluginKind newKind(String kind) {
        return new PluginKind(kind);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((kind == null) ? 0 : kind.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PluginKind)) {
            return false;
        }
        return kind.equals(((PluginKind) obj).kind);
    }
}