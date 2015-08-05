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
package org.specrunner.plugins;

/**
 * Stand for a plugin with concurrency constrains. If plugin implements this
 * interface the boolean threadsafe is the sign for the plugin perfom its
 * actions considering thread safe scenarios, for example in memory databases
 * can have their URLs changed to different threads.
 * 
 * @author Thiago Santos
 * 
 */
public interface IParalelPlugin extends IPlugin {

    /**
     * Set the thread safe status.
     * 
     * @param threadsafe
     *            The plugin thread safe flag.
     */
    void setThreadsafe(Boolean threadsafe);

    /**
     * The plugin thread safe status.
     * 
     * @return The thread safe status.
     */
    Boolean getThreadsafe();
}
