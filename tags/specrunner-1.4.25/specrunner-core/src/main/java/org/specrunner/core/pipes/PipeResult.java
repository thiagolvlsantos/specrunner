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

import org.specrunner.SRServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.result.IResultFactory;
import org.specrunner.result.IResultSet;

/**
 * Bind a result to the channel.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeResult implements IPipe {

    /**
     * Source.
     */
    public static final String RESULT = "result";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        bind(channel, createResult());
        return channel;
    }

    /**
     * Create a result instance.
     * 
     * @return A result set.
     */
    protected IResultSet createResult() {
        IResultSet result = SRServices.get(IResultFactory.class).newResult();
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(IResultSet.FEATURE_FULL_TRACE, result);
        fm.set(IResultSet.FEATURE_RESULT_FILTER, result);
        fm.set(IResultSet.FEATURE_RECORD_SUCCESS, result);
        fm.set(IResultSet.FEATURE_EXPECTED_MESSAGES, result);
        fm.set(IResultSet.FEATURE_EXPECTED_SORTED, result);
        fm.set(IResultSet.FEATURE_EXPECTED_CRITERIA, result);
        return result;
    }

    /**
     * Bind the result set to the channel.
     * 
     * @param channel
     *            The channel.
     * @param result
     *            The result set.
     */
    public static void bind(IChannel channel, IResultSet result) {
        channel.add(RESULT, result);
    }

    /**
     * Recover the result set from channel.
     * 
     * @param channel
     *            The channel.
     * @return The result set.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static IResultSet lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(RESULT, IResultSet.class);
    }
}
