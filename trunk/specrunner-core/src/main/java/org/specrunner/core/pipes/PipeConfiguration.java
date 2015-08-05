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

import org.specrunner.configuration.IConfiguration;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;

/**
 * Binds a configuration to the channel.
 * 
 * @author Thiago Santos
 * 
 */
public final class PipeConfiguration {

    /**
     * Configuration.
     */
    public static final String CONFIGURATION = "configuration";

    /**
     * Default constructor.
     */
    private PipeConfiguration() {
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
    public static IChannel bind(IChannel channel, IConfiguration obj) {
        return channel.add(CONFIGURATION, obj);
    }

    /**
     * Get the configuration from channel.
     * 
     * @param channel
     *            The channel.
     * @return The configuration.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On typing errors.
     */
    public static IConfiguration lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(CONFIGURATION, IConfiguration.class);
    }
}
