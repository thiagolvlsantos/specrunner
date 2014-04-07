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
package org.specrunner.core;

import org.specrunner.ISpecRunnerPlugin;
import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.core.pipes.PipeConfiguration;
import org.specrunner.core.pipes.PipeResult;
import org.specrunner.core.pipes.plugin.PipePlugin;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IChannelFactory;
import org.specrunner.pipeline.IPipeline;
import org.specrunner.pipeline.ProfilerPipeListener;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;

/**
 * Default implementation. All methods can be overridden!!!!
 * 
 * @author Thiago Santos
 * 
 */
public class SpecRunnerPluginPipeline implements ISpecRunnerPlugin {

    @Override
    public IResultSet run(IPlugin plugin) throws SpecRunnerException {
        return doRun(plugin, SRServices.get(IConfigurationFactory.class).newConfiguration());
    }

    @Override
    public IResultSet run(IPlugin plugin, IConfiguration configuration) throws SpecRunnerException {
        return doRun(plugin, configuration);
    }

    /**
     * Perform runner.
     * 
     * @param plugin
     *            The plugin instance.
     * @param configuration
     *            A configuration.
     * @return The result set.
     * @throws SpecRunnerException
     *             On execution errors.
     */
    protected IResultSet doRun(IPlugin plugin, IConfiguration configuration) throws SpecRunnerException {
        try {
            IChannel channel = SRServices.get(IChannelFactory.class).newChannel();
            PipeConfiguration.bind(PipePlugin.bind(channel, plugin), configuration);
            IPipeline pipe = SpecRunnerPipelineUtils.getPipeline(SRServices.get(), configuration, "specrunner_plugin.xml");
            pipe.addPipelineListener(new ProfilerPipeListener());
            return PipeResult.lookup(pipe.process(channel));
        } catch (Exception e) {
            throw new SpecRunnerException(e);
        }
    }
}