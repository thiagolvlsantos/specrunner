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

import org.specrunner.context.IModel;

/**
 * Stand for a plugin with sleep after execution.
 * 
 * @author Thiago Santos
 * 
 */
public interface ISleepPlugin extends IPlugin {

    /**
     * Gets time to sleep in milliseconds after test action. This sleep can be
     * programatically added as a 'sleep' attribute. The use of sleep can be
     * used for example, to overcome JavaScript problems, for example you can
     * set sleep to '4000' which means the action will wait this time before
     * leaving the ISpecRunner go on.
     * 
     * @param sleep
     *            Sleep time.
     */
    void setSleep(Long sleep);

    /**
     * The plugin timeout.
     * 
     * @return The timeout.
     */
    Long getSleep();

    /**
     * Return the sleep based on a model.
     * 
     * @return A model.
     */
    IModel<Object, Long> getSleepModel();

    /**
     * Set sleep model object.
     * 
     * @param sleepModel
     *            The model.
     */
    void setSleepModel(IModel<Object, Long> sleepModel);
}