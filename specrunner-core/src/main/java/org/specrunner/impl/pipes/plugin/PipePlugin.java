package org.specrunner.impl.pipes.plugin;

import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.plugins.IPlugin;

public class PipePlugin {

    /**
     * Configuration.
     */
    public static final String PLUGIN = "plugin";

    public static IChannel bind(IChannel channel, IPlugin input) throws PipelineException {
        channel.add(PLUGIN, input);
        return channel;
    }

    public static IPlugin recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(PLUGIN, IPlugin.class);
    }
}
