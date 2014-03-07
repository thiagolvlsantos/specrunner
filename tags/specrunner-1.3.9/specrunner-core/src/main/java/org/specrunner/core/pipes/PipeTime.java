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
package org.specrunner.core.pipes;

import java.util.Map;

import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

/**
 * Add time information to the channel.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeTime extends AbstractPipeModel {

    /**
     * Time model information.
     */
    public static final String TIME = "time";

    @Override
    public IChannel process(IChannel channel, Map<String, Object> model) throws PipelineException {
        model.put(TIME, System.currentTimeMillis());
        return channel;
    }

    /**
     * Recover the time information from channel.
     * 
     * @param channel
     *            The channel.
     * @return The time.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static Long lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return (Long) PipeModel.recover(channel).get(TIME);
    }
}