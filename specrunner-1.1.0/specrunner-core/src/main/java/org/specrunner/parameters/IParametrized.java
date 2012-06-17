/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.parameters;

import java.util.Map;

/**
 * Stand for anything that can have parameters set.
 * 
 * @author Thiago Santos
 * 
 */
public interface IParametrized {

    /**
     * Set a parameter.
     * 
     * @param name
     *            The parameter name.
     * @param value
     *            The parameter value.
     */
    void setParameter(String name, Object value);

    /**
     * Gets a parameter.
     * 
     * @param name
     *            The parameter name.
     * @return The parameter value.
     */
    Object getParameter(String name);

    /**
     * Check if a parameter name is valid.
     * 
     * @param name
     *            The parameter.
     * @return The parameter name.
     */
    boolean hasParameter(String name);

    /**
     * Map of parameters set. Only parameters properly set to object are
     * available.
     * 
     * @return The parameter mapping containing only parameters properly set.
     */
    Map<String, Object> getParameters();

    /**
     * Map of parameters set. All parameters tried to be set in the object, even
     * those whose corresponding property is not present in object.
     * 
     * @return The parameter mapping containing all parameters set.
     */
    Map<String, Object> getAllParameters();
}