/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.source.text;

/**
 * Stand for a scenario outline.
 * 
 * @author Thiago Santos
 * 
 */
public class ScenarioOutline extends Scenario {

    /**
     * The data table.
     */
    private DataTable table = new DataTable();

    /**
     * Create an outline with scenario.
     * 
     * @param name
     *            The name.
     */
    public ScenarioOutline(String name) {
        super(name);
    }

    /**
     * Get the examples.
     * 
     * @return The examples data.
     */
    public DataTable getTable() {
        return table;
    }

    /**
     * Set the table.
     * 
     * @param table
     *            The table.
     */
    public void setTable(DataTable table) {
        this.table = table;
    }

    @Override
    public String validate() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.validate());
        sb.append(table.validate());
        return sb.toString();
    }
}