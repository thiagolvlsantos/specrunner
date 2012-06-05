package org.specrunner.impl.pipes;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

public class PipeModel implements IPipe {

    /**
     * Model.
     */
    public static final String MODEL = "model";
    /**
     * Time model information.
     */
    public static final String TIME = "time";
    /**
     * Free model information.
     */
    public static final String FREE = "free";
    /**
     * Total model information.
     */
    public static final String TOTAL = "total";
    /**
     * Max model information.
     */
    public static final String MAX = "max";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        // create a model information
        channel.add(MODEL, create(channel));
        return channel;
    }

    /**
     * Creates the default model.
     * 
     * @param channel
     *            The channel.
     * @return A model mapping.
     * @throws InvalidTypeException
     *             On type error.
     * @throws NotFoundException
     *             On not found resources.
     */
    protected Map<String, Object> create(IChannel channel) throws NotFoundException, InvalidTypeException {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put(PipeInput.INPUT, PipeInput.lookup(channel));
        model.put(TIME, System.currentTimeMillis());
        Runtime rt = Runtime.getRuntime();
        model.put(FREE, rt.freeMemory());
        model.put(TOTAL, rt.totalMemory());
        model.put(MAX, rt.maxMemory());
        return model;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(MODEL, Map.class);
    }
}