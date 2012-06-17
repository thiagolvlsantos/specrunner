/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

/**
 * Adds a time stamp tag to the channel.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeTimestamp extends AbstractPipeModel {

    /**
     * Time model information.
     */
    public static final String DATE = "date";
    /**
     * Report date format.
     */
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public IChannel process(IChannel channel, Map<String, Object> model) throws PipelineException {
        model.put(PipeTime.TIME, System.currentTimeMillis() - (Long) model.get(PipeTime.TIME));
        model.put(DATE, sdf.format(new Date()));
        return channel;
    }

    /**
     * Recover time stamp from channel.
     * 
     * @param channel
     *            The channel.
     * @return The time stamp.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static Long lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return (Long) PipeModel.recover(channel).get(DATE);
    }
}