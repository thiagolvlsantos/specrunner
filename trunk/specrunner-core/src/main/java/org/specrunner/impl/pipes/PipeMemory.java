package org.specrunner.impl.pipes;

import java.util.Map;

import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

public class PipeMemory extends AbstractPipeModel {

    /**
     * Free information.
     */
    public static final String FREE = "free";
    /**
     * Total information.
     */
    public static final String TOTAL = "total";
    /**
     * Max information.
     */
    public static final String MAX = "max";

    @Override
    public IChannel process(IChannel channel, Map<String, Object> model) throws PipelineException {
        Runtime runtime = Runtime.getRuntime();
        channel.add(FREE, runtime.freeMemory());
        channel.add(TOTAL, runtime.totalMemory());
        channel.add(MAX, runtime.freeMemory());
        return channel;
    }

    public static Long recoverFree(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(FREE, Long.class);
    }

    public static Long recoverTotal(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(TOTAL, Long.class);
    }

    public static Long recoverMax(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(MAX, Long.class);
    }
}