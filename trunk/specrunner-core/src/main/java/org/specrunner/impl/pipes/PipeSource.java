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
package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactoryManager;
import org.specrunner.source.SourceException;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.source.resource.ResourceException;

/**
 * Creates the source and add to the channel.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeSource implements IPipe {

    /**
     * Source.
     */
    public static final String SOURCE = "source";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            ISource source = createSource(PipeInput.lookup(channel));
            addResources(source);
            bind(channel, source);
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Creates a source from input.
     * 
     * @param input
     *            The input name.
     * @return The source instance.
     * @throws SourceException
     *             On source creation errors.
     */
    protected ISource createSource(String input) throws SourceException {
        return SpecRunnerServices.get(ISourceFactoryManager.class).newSource(input);
    }

    /**
     * Add resources to a source.
     * 
     * @param source
     *            The source.
     * @throws ResourceException
     *             On resource errors.
     */
    protected void addResources(ISource source) throws ResourceException {
        IResourceManager manager = source.getManager();
        manager.addDefaultCss();
        manager.addDefaultJs();
    }

    /**
     * Bind a source to the channel.
     * 
     * @param channel
     *            The channel.
     * @param source
     *            The source.
     */
    public static void bind(IChannel channel, ISource source) {
        channel.add(SOURCE, source);
    }

    /**
     * Recover the source from channel.
     * 
     * @param channel
     *            The channel.
     * @return The source.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static ISource recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(SOURCE, ISource.class);
    }
}
