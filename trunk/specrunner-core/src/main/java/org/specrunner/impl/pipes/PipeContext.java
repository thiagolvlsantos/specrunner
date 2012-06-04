package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.ContextException;
import org.specrunner.context.IContext;
import org.specrunner.context.IContextFactory;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.runner.IRunner;
import org.specrunner.source.ISource;

public class PipeContext implements IPipe {

    /**
     * Source.
     */
    public static final String CONTEXT = "context";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            // for doRun(IPlugin m,...), the source cannot be present.
            ISource source = (ISource) channel.get(PipeSource.SOURCE);
            // a new context
            bind(channel, createContext(channel, source, PipeRunner.lookup(channel)));
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Creates a context based on a source and runner.
     * 
     * @param channel
     *            The channel.
     * @param source
     *            The input.
     * @param runner
     *            The runner.
     * @return Context errors.
     * @throws ContextException
     *             On context creation.
     */
    protected IContext createContext(IChannel channel, ISource source, IRunner runner) throws ContextException {
        return SpecRunnerServices.get(IContextFactory.class).newContext(channel, source, runner);
    }

    /**
     * Bind the object to the channel.
     * 
     * @param channel
     *            The channel.
     * @param obj
     *            The object.
     * @return The channel itself.
     */
    public static IChannel bind(IChannel channel, IContext obj) {
        return channel.add(CONTEXT, obj);
    }

    public static IContext recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(CONTEXT, IContext.class);
    }
}
