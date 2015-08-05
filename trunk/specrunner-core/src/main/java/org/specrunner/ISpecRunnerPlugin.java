package org.specrunner;

import org.specrunner.configuration.IConfiguration;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;

/**
 * Outermost interface of the API. Stands for an executor of specifications in
 * programmatic style.
 * 
 * @author Thiago Santos
 * 
 */
public interface ISpecRunnerPlugin {

    /**
     * Runs a plugin.
     * 
     * @param plugin
     *            The plugin source.
     * @return The result of execution.
     * @throws SpecRunnerException
     *             On execution errors.
     */
    IResultSet run(IPlugin plugin) throws SpecRunnerException;

    /**
     * Runs a specification using a given configuration.
     * 
     * @param plugin
     *            The plugin source.
     * @param configuration
     *            Specific configurations.
     * @return The result of execution.
     * @throws SpecRunnerException
     *             On execution errors.
     */
    IResultSet run(IPlugin plugin, IConfiguration configuration) throws SpecRunnerException;
}
