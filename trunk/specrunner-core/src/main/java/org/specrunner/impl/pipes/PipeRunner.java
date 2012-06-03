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

    public static void bind(IChannel channel, IRunner runner) throws NotFoundException, InvalidTypeException {
        channel.add(RUNNER, runner);
    }

    public static IRunner recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(RUNNER, IRunner.class);
    }
}
