package org.specrunner.impl.pipes.plugin;

import org.specrunner.SpecRunnerException;
import org.specrunner.impl.pipes.PipeContext;
import org.specrunner.impl.pipes.PipeResult;
import org.specrunner.impl.pipes.PipeRunner;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;

public class PipeRun implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            PipeRunner.recover(channel).run(PipePlugin.recover(channel), PipeContext.recover(channel), PipeResult.recover(channel));
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }
}