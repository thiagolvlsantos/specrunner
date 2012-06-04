package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.runner.IRunner;
import org.specrunner.runner.IRunnerFactory;
import org.specrunner.runner.RunnerException;
import org.specrunner.source.ISource;

public class PipeRunner implements IPipe {

    /**
     * Source.
     */
    public static final String RUNNER = "runner";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            // for doRun(IPlugin m,...), the source cannot be present.
            ISource source = (ISource) channel.get(PipeSource.SOURCE);
            bind(channel, createRunner(source));
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Creates a runner for a source.
     * 
     * @param source
     *            The source.
     * @return The corresponding runner.
     * @throws RunnerException
     *             On creation errors.
     */
    protected IRunner createRunner(ISource source) throws RunnerException {
        return SpecRunnerServices.get(IRunnerFactory.class).newRunner(source);
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
    public static IChannel bind(IChannel channel, IRunner obj) {
        return channel.add(RUNNER, obj);
    }

    public static IRunner lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(RUNNER, IRunner.class);
    }
}
