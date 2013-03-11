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
package org.specrunner.impl;

import org.specrunner.ISpecRunner;
import org.specrunner.ISpecRunnerFactory;

/**
 * Default factory implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class SpecRunnerFactoryImpl implements ISpecRunnerFactory {

    /**
     * The reusable runner.
     */
    protected ISpecRunner runner;

    /**
     * Creates a factory with a reusable runner.
     * 
     * @param runner
     *            The runner.
     */
    public SpecRunnerFactoryImpl(ISpecRunner runner) {
        setRunner(runner);
    }

    /**
     * Gets the runner.
     * 
     * @return The runner.
     */
    public ISpecRunner getRunner() {
        return runner;
    }

    /**
     * Sets the runner.
     * 
     * @param runner
     *            The runner.
     */
    public void setRunner(ISpecRunner runner) {
        this.runner = runner;
    }

    @Override
    public ISpecRunner newRunner() {
        return getRunner();
    }
}