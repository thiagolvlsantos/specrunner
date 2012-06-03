package org.specrunner.impl.pipes;

import org.specrunner.configuration.IConfiguration;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

public class PipeConfiguration {

    /**
     * Configuration.
     */
    public static final String CONFIGURATION = "configuration";

    public static IChannel bind(IChannel channel, IConfiguration configuration) throws PipelineException {
        channel.add(CONFIGURATION, configuration);
        return channel;
    }

    public static IConfiguration recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(CONFIGURATION, IConfiguration.class);
    }
}