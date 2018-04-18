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
package org.specrunner.junit;

import org.junit.Assert;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;

/**
 * JUnit use simplifier.
 * 
 * @author Thiago Santos
 * 
 */
public final class SpecRunnerJUnitPlugin {

    /**
     * Default constructor.
     */
    private SpecRunnerJUnitPlugin() {
    }

    /**
     * Basic execution.
     * 
     * @param input
     *            The plugin to be performed.
     */
    public static void defaultRun(IPlugin input) {
        defaultRun(input, SRServices.get(IConfigurationFactory.class).newConfiguration());
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
            IResultSet result = SRServices.getSpecRunnerPlugin().run(input, cfg);
            Assert.assertTrue(result.asString(), !result.getStatus().isError());
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            Assert.fail(e.getMessage());
        }
    }
}
