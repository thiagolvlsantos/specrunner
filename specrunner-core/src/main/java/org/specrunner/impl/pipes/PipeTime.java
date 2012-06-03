package org.specrunner.impl.pipes;

import java.util.Map;

import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

public class PipeTime extends AbstractPipeModel {

    /**
     * Time model information.
     */
    public static final String TIME = "time";

    @Override
    public IChannel process(IChannel channel, Map<String, Object> model) throws PipelineException {
        // create a model information
        model.put(TIME, System.currentTimeMillis());
        return channel;
    }

    public static Long recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return (Long) PipeModel.recover(channel).get(TIME);
    }
}