package org.specrunner.impl.pipes;

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