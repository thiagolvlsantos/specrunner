package org.specrunner.impl.pipes;

import java.util.Map;

import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;

/**
 * A pipe with information about a model.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPipeModel implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        return process(channel, PipeModel.recover(channel));
    }

    /**
     * Process the channel with the model already recovered.
     * 
     * @param channel
     *            The pipeline channel.
     * @param model
     *            The model.
     * @return The channel itself.
     * @throws PipelineException
     *             On pipeline errors.
     */
    public abstract IChannel process(IChannel channel, Map<String, Object> model) throws PipelineException;

}