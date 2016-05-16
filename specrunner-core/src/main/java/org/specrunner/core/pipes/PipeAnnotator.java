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
import org.specrunner.annotator.AnnotatorException;
import org.specrunner.annotator.IAnnotator;
import org.specrunner.annotator.IAnnotatorFactory;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

/**
 * Pipe to bind an annotator to the channel.
 * 
 * @author Thiago Santos.
 * 
 */
public class PipeAnnotator implements IPipe {

    /**
     * Object name into the channel.
     */
    public static final String ANNOTATOR = "annotator";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            bind(channel, createAnnotator());
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Creates an annotator instance.
     * 
     * @return A annotator.
     * @throws AnnotatorException
     *             On creation error.
     */
    protected IAnnotator createAnnotator() throws AnnotatorException {
        return SRServices.get(IAnnotatorFactory.class).newAnnotator();
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
    public static IChannel bind(IChannel channel, IAnnotator obj) {
        return channel.add(ANNOTATOR, obj);
    }

    /**
     * Look for the object into the channel.
     * 
     * @param channel
     *            The channel.
     * @return The object.
     * @throws NotFoundException
     *             When object with the given name.
     * @throws InvalidTypeException
     *             When expected type do not match.
     */
    public static IAnnotator lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(ANNOTATOR, IAnnotator.class);
    }
}
