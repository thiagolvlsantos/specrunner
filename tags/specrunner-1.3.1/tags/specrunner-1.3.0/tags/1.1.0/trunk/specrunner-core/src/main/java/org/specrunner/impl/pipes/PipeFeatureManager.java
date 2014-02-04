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

import org.specrunner.SpecRunnerServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

/**
 * Set feature manager into channel.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeFeatureManager implements IPipe {

    /**
     * Feature manager.
     */
    public static final String FEATURE_MANAGER = "featureManager";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        try {
            PipeConfiguration.lookup(channel);
        } catch (NotFoundException e) {
            throw new AbortException(e);
        } catch (InvalidTypeException e) {
            throw new AbortException(e);
        }
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        IFeatureManager features = createFeatureManager();

        features.setConfiguration(PipeConfiguration.lookup(channel));

        channel.add(FEATURE_MANAGER, features);

        return channel;
    }

    /**
     * Creates a feature manager.
     * 
     * @return A manager.
     */
    protected IFeatureManager createFeatureManager() {
        return SpecRunnerServices.get(IFeatureManager.class);
    }

    /**
     * Recover the manager from channel.
     * 
     * @param channel
     *            The channel.
     * @return The feature manager.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static IFeatureManager lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(FEATURE_MANAGER, IFeatureManager.class);
    }
}
