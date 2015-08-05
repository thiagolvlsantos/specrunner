/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.plugins.core;

import org.specrunner.parameters.DontEval;

/**
 * A plugin with name information.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginNamed extends AbstractPlugin {

    /**
     * The plugin instance name.
     */
    protected String name;

    /**
     * Name attribute.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a name.
     * 
     * @param name
     *            A new name.
     */
    @DontEval
    public void setName(String name) {
        this.name = name;
    }
}
