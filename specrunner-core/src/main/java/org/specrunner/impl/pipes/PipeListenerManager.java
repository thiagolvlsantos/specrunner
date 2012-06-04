package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerServices;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

public class PipeListenerManager implements IPipe {

    /**
     * Model.
     */
    public static final String LISTENER_MANAGER = "listenerManager";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        // listeners added
        IListenerManager listeners = createListenerManager();

        // reset listeners
        listeners.reset();

        channel.add(LISTENER_MANAGER, listeners);

        return channel;
    }

    /**
     * Creates a listener manager.
     * 
     * @return A manager.
     */
    protected IListenerManager createListenerManager() {
        return SpecRunnerServices.get(IListenerManager.class);
    }

    public static IListenerManager lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(LISTENER_MANAGER, IListenerManager.class);
    }
}
