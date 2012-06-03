package org.specrunner.impl.pipes;

import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;

public class PipeReport implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        PipeReporter.recover(channel).add(PipeResult.recover(channel), PipeModel.recover(channel));
        return channel;
    }
}
