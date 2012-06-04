package org.specrunner.impl.pipes.plugin;

import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.plugins.IPlugin;

public class PipePlugin {

    /**
     * Configuration.
     */
    public static final String PLUGIN = "plugin";

    /**
     * Bind the object to the channel.
     * 
     * @param channel
     *            The channel.
     * @param obj
     *            The object.
     * @return The channel itself.
     */
    public static IChannel bind(IChannel channel, IPlugin obj) {
        return channel.add(PLUGIN, obj);
    }

    public static IPlugin recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(PLUGIN, IPlugin.class);
    }
}
