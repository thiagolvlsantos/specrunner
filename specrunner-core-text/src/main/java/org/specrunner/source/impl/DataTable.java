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
 * Stand for a data table.
 * 
 * @author Thiago Santos.
 * 
 */
public class DataTable {

    /**
     * The parameter names.
     */
    private List<String> names = new LinkedList<String>();
    /**
     * The list of example elements.
     */
    private List<List<String>> examples = new LinkedList<List<String>>();

    /**
     * Get the parameter names.
     * 
     * @return The list.
     */
    public List<String> getNames() {
        return names;
    }

    /**
     * Set the parameter names.
     * 
     * @param names
     *            The names.
     */
    public void setNames(List<String> names) {
        this.names = names;
    }

    /**
     * Add a parameter name.
     * 
     * @param name
     *            A name.
     * @return The object itself.
     */
    public DataTable add(String name) {
        names.add(name);
        return this;
    }

    /**
     * Get the list of examples line.
     * 
     * @return The list.
     */
    public List<List<String>> getExamples() {
        return examples;
    }

    /**
     * Set the examples list.
     * 
     * @param examples
     *            The examples.
     */
    public void setExamples(List<List<String>> examples) {
        this.examples = examples;
    }

    /**
     * Add a list of examples.
     * 
     * @param example
     *            The example.
     * @return The object itself.
     */
    public DataTable add(List<String> example) {
        examples.add(example);
        return this;
    }

    /**
     * Validate data table.
     * 
     * @return The error message, if any.
     */
    public String validate() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (List<String> e : examples) {
            if (names.size() != e.size()) {
                sb.append("\n Example[" + index + "] size is different of expected arguments, args: " + names + ", example:" + e);
            }
            index++;
        }
        return sb.toString();
    }
}
