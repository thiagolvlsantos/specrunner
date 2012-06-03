package org.specrunner.impl.pipes.shutdown;

import org.specrunner.SpecRunnerServices.ShutDown;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReusableManager;

public class PipeReusable implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws AbortException, PipelineException {
        IReusableManager rm = ShutDown.recover(channel).lookup(IReusableManager.class);
        for (IReusable<?> r : rm.values()) {
            r.release();
            rm.remove(r);
        }
        return channel;
    }
}
