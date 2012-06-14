package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.dumper.ISourceDumper;
import org.specrunner.dumper.ISourceDumperFactory;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

/**
 * Adds a dumper to the channel.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeDumper implements IPipe {

    /**
     * Dumper.
     */
    public static final String DUMPER = "dumper";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            bind(channel, createDumper());
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Creates an dumper instance.
     * 
     * @return A dumper.
     * @throws SourceDumperException
     *             On creation error.
     */
    protected ISourceDumper createDumper() throws SourceDumperException {
        return SpecRunnerServices.get(ISourceDumperFactory.class).newDumper();
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
    public static IChannel bind(IChannel channel, ISourceDumper obj) {
        return channel.add(DUMPER, obj);
    }

    /**
     * Recover a dumper from channel.
     * 
     * @param channel
     *            The channel.
     * @return The dumper.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static ISourceDumper lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(DUMPER, ISourceDumper.class);
    }
}
