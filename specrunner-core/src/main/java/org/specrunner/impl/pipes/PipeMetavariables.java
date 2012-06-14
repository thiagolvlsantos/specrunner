package org.specrunner.impl.pipes;

import java.util.Map.Entry;

import org.specrunner.context.IContext;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;

public class PipeMetavariables implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        IContext context = PipeContext.lookup(channel);
        for (Entry<String, Object> e : channel.entrySet()) {
            context.saveGlobal(e.getKey(), e.getValue());
        }
        return channel;
    }
}