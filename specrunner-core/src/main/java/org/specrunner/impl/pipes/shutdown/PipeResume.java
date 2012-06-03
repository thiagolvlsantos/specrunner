package org.specrunner.impl.pipes.shutdown;

import org.specrunner.SpecRunnerServices.ShutDown;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.report.IReporterFactory;

public class PipeResume implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws AbortException, PipelineException {
        IReporterFactory r = ShutDown.recover(channel).lookup(IReporterFactory.class);
        r.newReporter().dump();
        return channel;
    }
}
