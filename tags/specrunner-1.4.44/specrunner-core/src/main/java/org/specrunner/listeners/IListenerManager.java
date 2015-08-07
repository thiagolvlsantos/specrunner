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
package org.specrunner.listeners;

import java.util.List;

/**
 * A listeners manager.
 * 
 * @author Thiago Santos
 * 
 */
public interface IListenerManager extends List<ISpecRunnerListener> {

    /**
     * Reset all listeners.
     */
    void reset();

    /**
     * Remove a listener by its name.
     * 
     * @param name
     *            The name of the listener to be removed.
     */
    void remove(String name);

    /**
     * Filter listeners by their types.
     * 
     * @param <T>
     *            The listener type.
     * @param type
     *            The type to be filtered.
     * @return The listeners of the given type.
     */
    <T extends ISpecRunnerListener> List<T> filterByType(Class<T> type);
}
