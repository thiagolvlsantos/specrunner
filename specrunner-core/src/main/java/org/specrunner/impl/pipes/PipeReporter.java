package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.report.IReporter;
import org.specrunner.report.IReporterFactory;

public class PipeReporter implements IPipe {

    /**
     * Source.
     */
    public static final String REPORTER = "reporter";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            bind(channel, createReporter());
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Creates a reporter instance.
     * 
     * @return A reporter.
     * @throws SourceDumperException
     *             On creation error.
     */
    protected IReporter createReporter() throws SourceDumperException {
        return SpecRunnerServices.get(IReporterFactory.class).newReporter();
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
    public static IChannel bind(IChannel channel, IReporter obj) {
        return channel.add(REPORTER, obj);
    }

    public static IReporter lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(REPORTER, IReporter.class);
    }
}