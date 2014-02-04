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
package org.specrunner.impl;

import org.specrunner.ISpecRunnerFactoryPlugin;
import org.specrunner.ISpecRunnerPlugin;

/**
 * Default factory implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class SpecRunnerFactoryPluginImpl implements ISpecRunnerFactoryPlugin {

    /**
     * The reusable runner.
     */
    protected ISpecRunnerPlugin runner;

    /**
     * Creates a factory with a reusable runner.
     * 
     * @param runner
     *            The runner.
     */
    public SpecRunnerFactoryPluginImpl(ISpecRunnerPlugin runner) {
        setRunner(runner);
    }

    /**
     * Gets the runner.
     * 
     * @return The runner.
     */
    public ISpecRunnerPlugin getRunner() {
        return runner;
    }

    /**
     * Sets the runner.
     * 
     * @param runner
     *            The runner.
     */
    public void setRunner(ISpecRunnerPlugin runner) {
        this.runner = runner;
    }

    @Override
    public ISpecRunnerPlugin newRunner() {
        return getRunner();
    }
}