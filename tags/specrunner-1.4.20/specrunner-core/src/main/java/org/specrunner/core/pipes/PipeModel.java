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

import java.util.HashMap;
import java.util.Map;

import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.util.UtilLog;

/**
 * Add a model information to the channel.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeModel implements IPipe {

    /**
     * Model.
     */
    public static final String MODEL = "model";
    /**
     * Time model information.
     */
    public static final String TIME = "time";
    /**
     * Free model information.
     */
    public static final String FREE = "free";
    /**
     * Total model information.
     */
    public static final String TOTAL = "total";
    /**
     * Max model information.
     */
    public static final String MAX = "max";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        // create a model information
        channel.add(MODEL, create(channel));
        return channel;
    }

    /**
     * Creates the default model.
     * 
     * @param channel
     *            The channel.
     * @return A model mapping.
     * @throws InvalidTypeException
     *             On type error.
     * @throws NotFoundException
     *             On not found resources.
     */
    protected Map<String, Object> create(IChannel channel) throws NotFoundException, InvalidTypeException {
        Map<String, Object> model = new HashMap<String, Object>();
        try {
            model.put(PipeInput.INPUT, PipeInput.lookup(channel));
        } catch (Exception e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        }
        model.put(TIME, System.currentTimeMillis());
        Runtime rt = Runtime.getRuntime();
        model.put(FREE, rt.freeMemory());
        model.put(TOTAL, rt.totalMemory());
        model.put(MAX, rt.maxMemory());
        return model;
    }

    /**
     * Recover the model from channel.
     * 
     * @param channel
     *            The channel.
     * @return The model.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(MODEL, Map.class);
    }
}