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

import java.io.File;

import org.specrunner.ISpecRunner;
import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.core.pipes.PipeConfiguration;
import org.specrunner.core.pipes.PipeInput;
import org.specrunner.core.pipes.PipeResult;
import org.specrunner.dumper.core.AbstractSourceDumperFile;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IChannelFactory;
import org.specrunner.pipeline.IPipeline;
import org.specrunner.result.IResultSet;

/**
 * Default implementation. All methods can be overridden!!!!
 * 
 * @author Thiago Santos
 * 
 */
public class SpecRunnerPipeline implements ISpecRunner {

    @Override
    public IResultSet run(String input) throws SpecRunnerException {
        return doRun(input, SRServices.get(IConfigurationFactory.class).newConfiguration());
    }

    @Override
    public IResultSet run(String input, IConfiguration configuration) throws SpecRunnerException {
        return doRun(input, configuration);
    }

    @Override
    public IResultSet run(String input, String output, IConfiguration configuration) throws SpecRunnerException {
        File file = new File(output);
        configuration.add(AbstractSourceDumperFile.FEATURE_OUTPUT_DIRECTORY, file.getParentFile());
        configuration.add(AbstractSourceDumperFile.FEATURE_OUTPUT_NAME, file.getName());
        return doRun(input, configuration);
    }

    /**
     * Perform a specification given by input.
     * 
     * @param input
     *            An input.
     * @param configuration
     *            A configuration.
     * @return The result set.
     * @throws SpecRunnerException
     *             On runner errors.
     */
    protected IResultSet doRun(String input, IConfiguration configuration) throws SpecRunnerException {
        try {
            IChannel channel = SRServices.get(IChannelFactory.class).newChannel();
            PipeConfiguration.bind(PipeInput.bind(channel, input), configuration);
            IPipeline pipe = SpecRunnerPipelineUtils.getPipeline(SRServices.get(), configuration, "specrunner.xml");
            return PipeResult.lookup(pipe.process(channel));
        } catch (Exception e) {
            throw new SpecRunnerException(e);
        }
    }
}