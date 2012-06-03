package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerServices;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.result.IResultFactory;
import org.specrunner.result.IResultSet;

public class PipeResult implements IPipe {

    /**
     * Source.
     */
    public static final String RESULT = "result";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        bind(channel, createResult());
        return channel;
    }

    /**
     * Create a result instance.
     * 
     * @return A result set.
     */
    protected IResultSet createResult() {
        return SpecRunnerServices.get(IResultFactory.class).newResult();
    }

    public static void bind(IChannel channel, IResultSet result) throws NotFoundException, InvalidTypeException {
        channel.add(RESULT, result);
    }

    public static IResultSet recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(RESULT, IResultSet.class);
    }
}
