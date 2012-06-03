package org.specrunner.impl.pipes.plugin;

import java.util.Map;

import org.specrunner.impl.pipes.PipeDump;
import org.specrunner.impl.pipes.PipeModel;
import org.specrunner.impl.pipes.PipeResult;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.result.IResultSet;

public class PipeShortDump extends PipeDump {

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        // message before dump
        messageBefore(PipePlugin.recover(channel).toString());

        // message after dump
        IResultSet result = PipeResult.recover(channel);
        Map<String, Object> model = PipeModel.recover(channel);
        messageAfter(model, result);
        return channel;
    }
}
