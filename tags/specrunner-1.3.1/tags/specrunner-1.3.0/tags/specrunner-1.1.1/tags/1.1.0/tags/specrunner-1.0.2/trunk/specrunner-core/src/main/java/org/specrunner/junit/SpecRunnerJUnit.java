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
package org.specrunner.junit;

import java.io.File;

import org.junit.Assert;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.dumper.impl.AbstractSourceDumperFile;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;

/**
 * JUnit use simplifier.
 * 
 * @author Thiago Santos
 * 
 */
public final class SpecRunnerJUnit {

    /**
     * Hidden constructor.
     */
    private SpecRunnerJUnit() {
    }

    /**
     * Basic execution.
     * 
     * @param input
     *            The specification file.
     */
    public static void defaultRun(String input) {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        defaultRun(input, cfg);
    }

    /**
     * Execution with a given configuration.
     * 
     * @param input
     *            The specification file.
     * @param cfg
     *            The configuration.
     */
    public static void defaultRun(String input, IConfiguration cfg) {
        try {
            IResultSet result = SpecRunnerServices.getSpecRunner().run(input, cfg);
            Assert.assertTrue(result.asString(), !result.getStatus().isError());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Basic execution.
     * 
     * @param input
     *            The plugin to be performed.
     */
    public static void defaultRun(IPlugin input) {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        defaultRun(input, cfg);
    }

    /**
     * Execution with a given configuration.
     * 
     * @param input
     *            The specification file.
     * @param cfg
     *            The configuration.
     */
    public static void defaultRun(IPlugin input, IConfiguration cfg) {
        try {
            IResultSet result = SpecRunnerServices.getSpecRunner().run(input, cfg);
            Assert.assertTrue(result.asString(), !result.getStatus().isError());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * A basic execution with a different output file.
     * 
     * @param input
     *            The specification file.
     * @param output
     *            The specification output file.
     */
    public static void defaultRun(String input, String output) {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        defaultRun(input, output, cfg);
    }

    /**
     * A configured execution with a different output file.
     * 
     * @param input
     *            The specification file.
     * @param output
     *            The specification output file.
     * @param cfg
     *            The configuration.
     */
    public static void defaultRun(String input, String output, IConfiguration cfg) {
        File file = new File(output);
        cfg.add(AbstractSourceDumperFile.FEATURE_OUTPUT_DIRECTORY, file.getParentFile());
        cfg.add(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, file.getName());
        defaultRun(input, cfg);
    }
}