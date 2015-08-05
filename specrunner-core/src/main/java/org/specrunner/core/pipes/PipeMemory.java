/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
 * Add memory information to the pipe.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeMemory extends AbstractPipeModel {

    /**
     * Free information.
     */
    public static final String FREE = "free";
    /**
     * Total information.
     */
    public static final String TOTAL = "total";
    /**
     * Max information.
     */
    public static final String MAX = "max";

    @Override
    public IChannel process(IChannel channel, Map<String, Object> model) throws PipelineException {
        Runtime runtime = Runtime.getRuntime();
        channel.add(FREE, runtime.freeMemory());
        channel.add(TOTAL, runtime.totalMemory());
        channel.add(MAX, runtime.freeMemory());
        return channel;
    }

    /**
     * Get the free memory information.
     * 
     * @param channel
     *            The channel.
     * @return The value.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static Long lookupFree(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(FREE, Long.class);
    }

    /**
     * Get the total memory information.
     * 
     * @param channel
     *            The channel.
     * @return The value.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static Long lookupTotal(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(TOTAL, Long.class);
    }

    /**
     * Get the max memory information.
     * 
     * @param channel
     *            The channel.
     * @return The value.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static Long lookupMax(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(MAX, Long.class);
    }
}
