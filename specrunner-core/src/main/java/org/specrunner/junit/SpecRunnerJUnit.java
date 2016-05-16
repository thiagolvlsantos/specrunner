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

import java.io.File;

import org.junit.Assert;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.dumper.core.ConstantsDumperFile;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;

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
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
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
            IResultSet result = SRServices.getSpecRunner().run(input, cfg);
            File dir = (File) SRServices.getFeatureManager().get(ConstantsDumperFile.FEATURE_OUTPUT_DIRECTORY);
            String file = (String) SRServices.getFeatureManager().get(ConstantsDumperFile.FEATURE_OUTPUT_NAME);
            Assert.assertTrue((dir != null && file != null ? "OUTPUT: " + new File(dir, file).getAbsolutePath() + "\n" : "") + result.asString(), !result.getStatus().isError());
        } catch (Exception e) {
            e.printStackTrace();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
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
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
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
        cfg.add(ConstantsDumperFile.FEATURE_OUTPUT_DIRECTORY, file.getParentFile());
        cfg.add(ConstantsDumperFile.FEATURE_OUTPUT_NAME, file.getName());
        defaultRun(input, cfg);
    }
}
