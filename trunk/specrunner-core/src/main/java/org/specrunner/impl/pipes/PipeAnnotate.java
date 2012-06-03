package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;

public class PipeAnnotate implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            PipeAnnotator.recover(channel).annotate(PipeResult.recover(channel));
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }
}
