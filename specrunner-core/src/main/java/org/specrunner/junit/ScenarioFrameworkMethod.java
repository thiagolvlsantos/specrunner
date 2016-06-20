/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.junit;

import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;

/**
 * Extension to hold scenario names.
 * 
 * @author Thiago Santos
 * 
 */
public class ScenarioFrameworkMethod extends FrameworkMethod {

    /**
     * A scenario name.
     */
    protected String name;

    /**
     * A scenario name.
     * 
     * @param method
     *            A method.
     * @param name
     *            A scenario name.
     */
    public ScenarioFrameworkMethod(Method method, String name) {
        super(method);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the scenario name.
     * 
     * @param name
     *            The scenario name.
     */
    public void setName(String name) {
        this.name = name;
    }
}
