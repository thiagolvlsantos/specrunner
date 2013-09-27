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

import java.util.LinkedList;
import java.util.List;

/**
 * Any object with a name as a group os sentences.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class Description {

    /**
     * Group name.
     */
    protected String name;
    /**
     * Sentences set.
     */
    protected List<String> description = new LinkedList<String>();

    /**
     * The object name.
     * 
     * @param name
     *            The name.
     */
    public Description(String name) {
        this.name = name;
    }

    /**
     * Get the name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     * 
     * @param name
     *            The name.
     * @return The object itself.
     */
    public Description setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the list of sentences.
     * 
     * @return The list.
     */
    public List<String> getDescription() {
        return description;
    }

    /**
     * Set the sentences list.
     * 
     * @param description
     *            The descriptions.
     * @return The object itself.
     */
    public Description setDescription(List<String> description) {
        this.description = description;
        return this;
    }

    /**
     * Add a description.
     * 
     * @param description
     *            A description.
     * @return The object itself.
     */
    public Description addDescription(String description) {
        this.description.add(description);
        return this;
    }

    /**
     * Perform object validation.
     * 
     * @return The description of errors, if any, empty string otherwise.
     */
    public abstract String validate();
}