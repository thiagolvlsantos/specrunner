/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.core.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SRServices;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.runner.IRunner;
import org.specrunner.runner.IRunnerFactory;
import org.specrunner.runner.RunnerException;
import org.specrunner.source.ISource;

/**
 * Add a runner to the pipe.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeRunner implements IPipe {

    /**
     * Source.
     */
    public static final String RUNNER = "runner";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            // for doRun(IPlugin m,...), the source cannot be present.
            ISource source = (ISource) channel.get(PipeSource.SOURCE);
            bind(channel, createRunner(source));
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Creates a runner for a source.
     * 
     * @param source
     *            The source.
     * @return The corresponding runner.
     * @throws RunnerException
     *             On creation errors.
     */
    protected IRunner createRunner(ISource source) throws RunnerException {
        return SRServices.get(IRunnerFactory.class).newRunner(source);
    }

    /**
     * Bind the object to the channel.
     * 
     * @param channel
     *            The channel.
     * @param obj
     *            The object.
     * @return The channel itself.
     */
    public static IChannel bind(IChannel channel, IRunner obj) {
        return channel.add(RUNNER, obj);
    }

    /**
     * Recover a runner from channel.
     * 
     * @param channel
     *            The channel.
     * @return The runner.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type erros.
     */
    public static IRunner lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(RUNNER, IRunner.class);
    }
}
