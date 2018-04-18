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
 * Stand for a plugin with sleep before execution.
 * 
 * @author Thiago Santos
 * 
 */
public interface IWaitPlugin extends IPlugin {

    /**
     * Gets time to sleep in milliseconds before test action. This wait can be
     * programatically added as a 'wait' attribute. The use of wait can be used
     * for example, to overcome JavaScript problems, for example you can set
     * wait to '4000' which means ISpecRunner will wait this time before go on.
     * 
     * @param wait
     *            Wait time.
     */
    void setWait(Long wait);

    /**
     * The plugin wait time.
     * 
     * @return The wait time.
     */
    Long getWait();

    /**
     * Return the wait based on a model.
     * 
     * @return A model.
     */
    IModel<Long> getWaitModel();

    /**
     * Set wait model object.
     * 
     * @param waitModel
     *            The model.
     */
    void setWaitModel(IModel<Long> waitModel);
}
