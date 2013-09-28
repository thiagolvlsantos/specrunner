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
package org.specrunner.source.impl;

/**
 * Stand for a scenario.
 * 
 * @author Thiago Santos
 * 
 */
public class Scenario extends NamedSentence {

    /**
     * The parent feature.
     */
    private Feature parent;

    /**
     * Create a scenario with a name.
     * 
     * @param name
     *            A name.
     */
    public Scenario(String name) {
        super(name);
    }

    /**
     * Get the feature parent.
     * 
     * @return The parent.
     */
    public Feature getParent() {
        return parent;
    }

    /**
     * Set the parent.
     * 
     * @param parent
     *            The scenario parent.
     */
    public void setParent(Feature parent) {
        this.parent = parent;
    }

    @Override
    public String validate() {
        StringBuilder sb = new StringBuilder();
        if (parent == null) {
            sb.append("\n" + getName() + ": missing feature reference.");
        }
        return sb.toString();
    }
}