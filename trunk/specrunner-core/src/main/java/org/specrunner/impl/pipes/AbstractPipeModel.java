package org.specrunner.impl.pipes;

import java.util.Map;

import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;

public abstract class AbstractPipeModel implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        return process(channel, PipeModel.recover(channel));
    }

    public abstract IChannel process(IChannel channel, Map<String, Object> recover) throws PipelineException;

}