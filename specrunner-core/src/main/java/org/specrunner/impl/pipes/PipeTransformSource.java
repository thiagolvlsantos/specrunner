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

import org.specrunner.SpecRunnerServices;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.transformer.ITransformer;

/**
 * Pipe perform a transformation into sources.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeTransformSource implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            // performs source transformation.
            PipeSource.bind(channel, transformSource(PipeSource.recover(channel)));
        } catch (SourceException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Perform a transformation of a given source.
     * 
     * @param source
     *            The source to be transformed.
     * @return The transformed source.
     * @throws SourceException
     *             On transformation errors.
     */
    protected ISource transformSource(ISource source) throws SourceException {
        return SpecRunnerServices.get(ITransformer.class).transform(source);
    }
}