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

import java.util.Map;

import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;

/**
 * A pipe with information about a model.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPipeModel implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        return process(channel, PipeModel.recover(channel));
    }

    /**
     * Process the channel with the model already recovered.
     * 
     * @param channel
     *            The pipeline channel.
     * @param model
     *            The model.
     * @return The channel itself.
     * @throws PipelineException
     *             On pipeline errors.
     */
    public abstract IChannel process(IChannel channel, Map<String, Object> model) throws PipelineException;

}
