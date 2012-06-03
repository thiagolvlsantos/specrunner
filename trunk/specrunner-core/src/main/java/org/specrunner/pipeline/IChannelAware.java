package org.specrunner.pipeline;


public interface IChannelAware {

    IChannel getChannel();

    void setChannel(IChannel channel);
}