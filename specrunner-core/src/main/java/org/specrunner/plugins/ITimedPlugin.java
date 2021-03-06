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
 * Stand for a plugin with time constrains. If <code>doStart()</code> or
 * <code>doEnd()</code> is performed in time superior to timeout an error is
 * thrown.
 * 
 * @author Thiago Santos
 * 
 */
public interface ITimedPlugin extends IPlugin {

    /**
     * Set the plugin timeout.
     * 
     * @param timeout
     *            The plugin timeout.
     */
    void setTimeout(Long timeout);

    /**
     * The plugin timeout.
     * 
     * @return The timeout.
     */
    Long getTimeout();

    /**
     * Model for timeout testing.
     * 
     * @return The timeout model.
     */
    IModel<Long> getTimeoutModel();

    /**
     * Set the model.
     * 
     * @param timeoutModel
     *            A time model.
     */
    void setTimeoutModel(IModel<Long> timeoutModel);
}
