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
package org.specrunner;

import org.specrunner.configuration.IConfiguration;
import org.specrunner.result.IResultSet;

/**
 * Outermost interface of the API. Stands for an executor of specifications.
 * 
 * @author Thiago Santos
 * 
 */
public interface ISpecRunner {

    /**
     * Runs a specification.
     * 
     * @param source
     *            The specification source.
     * @return The result of execution.
     * @throws SpecRunnerException
     *             On execution errors.
     */
    IResultSet run(String source) throws SpecRunnerException;

    /**
     * Runs a specification using a given configuration.
     * 
     * @param source
     *            The specification source.
     * @param configuration
     *            Specific configurations.
     * @return The result of execution.
     * @throws SpecRunnerException
     *             On execution errors.
     */
    IResultSet run(String source, IConfiguration configuration) throws SpecRunnerException;

    /**
     * Runs a specification using a given configuration.
     * 
     * @param source
     *            The specification source.
     * @param output
     *            The specification output.
     * @param configuration
     *            Specific configurations.
     * @return The result of execution.
     * @throws SpecRunnerException
     *             On execution errors.
     */
    IResultSet run(String source, String output, IConfiguration configuration) throws SpecRunnerException;
}
