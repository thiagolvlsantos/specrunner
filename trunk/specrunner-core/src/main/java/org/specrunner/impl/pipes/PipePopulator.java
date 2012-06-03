package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.ContextException;
import org.specrunner.context.IContext;
import org.specrunner.context.IContextPopulator;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;

public class PipePopulator implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            PipeContext.bind(channel, populate(PipeContext.recover(channel)));
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Populate a context with predefined values.
     * 
     * @param context
     *            The context to be populated.
     * @return The context populated.
     * @throws ContextException
     *             On populate errors.
     */
    protected IContext populate(IContext context) throws ContextException {
        return SpecRunnerServices.get(IContextPopulator.class).populate(context);
    }
}
