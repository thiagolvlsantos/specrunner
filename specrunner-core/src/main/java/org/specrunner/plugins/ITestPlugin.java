/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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

import org.specrunner.context.IModel;

/**
 * Perform a plugin execution based on a condition.
 * 
 * @author Thiago Santos
 * 
 */
public interface ITestPlugin extends IPlugin {

    /**
     * Get the plugin execution condition.
     * 
     * @return The condition is some expression which returns true, or false.
     */
    Boolean getCondition();

    /**
     * Set plugin condition.
     * 
     * @param condition
     *            The plugin condition.
     */
    void setCondition(Boolean condition);

    /**
     * Get the plugin execution condition model.
     * 
     * @return The condition is some expression which returns true, or false.
     */
    IModel<Boolean> getConditionModel();

    /**
     * Set plugin condition model.
     * 
     * @param model
     *            The plugin condition model.
     */
    void setConditionModel(IModel<Boolean> model);
}
