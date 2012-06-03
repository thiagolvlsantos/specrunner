package org.specrunner.impl.pipes;

import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

public class PipeInput {

    /**
     * Configuration.
     */
    public static final String INPUT = "input";

    public static IChannel bind(IChannel channel, String input) throws PipelineException {
        channel.add(INPUT, input);
        return channel;
    }

    public static String recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(INPUT, String.class);
    }
}