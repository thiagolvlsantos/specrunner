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
package org.specrunner.core.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SRServices;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.report.IReporter;
import org.specrunner.report.IReporterFactory;

/**
 * Creates a reporter and add to the channel.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeReporter implements IPipe {

    /**
     * Source.
     */
    public static final String REPORTER = "reporter";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            bind(channel, createReporter());
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Creates a reporter instance.
     * 
     * @return A reporter.
     * @throws SourceDumperException
     *             On creation error.
     */
    protected IReporter createReporter() throws SourceDumperException {
        return SRServices.get(IReporterFactory.class).newReporter();
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
    public static IChannel bind(IChannel channel, IReporter obj) {
        return channel.add(REPORTER, obj);
    }

    /**
     * Recover the reporter from channel.
     * 
     * @param channel
     *            The channel.
     * @return The reporter.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static IReporter lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(REPORTER, IReporter.class);
    }
}
