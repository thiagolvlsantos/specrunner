package org.specrunner.junit;

import org.junit.Assert;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;

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
            IResultSet result = SpecRunnerServices.getSpecRunnerPlugin().run(input, cfg);
            Assert.assertTrue(result.asString(), !result.getStatus().isError());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
