package org.specrunner.impl.pipes;

import org.specrunner.configuration.IConfiguration;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;

public class PipeConfiguration {

    /**
     * Configuration.
     */
    public static final String CONFIGURATION = "configuration";

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

    public static IConfiguration recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(CONFIGURATION, IConfiguration.class);
    }
}