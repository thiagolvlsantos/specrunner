package org.specrunner.impl.pipes;

import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;

public class PipeInput {

    /**
     * Configuration.
     */
    public static final String INPUT = "input";

    /**
     * Bind the object to the channel.
     * 
     * @param channel
     *            The channel.
     * @param obj
     *            The object.
     * @return The channel itself.
     */
    public static IChannel bind(IChannel channel, String obj) {
        return channel.add(INPUT, obj);
    }

    public static String lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(INPUT, String.class);
    }
}