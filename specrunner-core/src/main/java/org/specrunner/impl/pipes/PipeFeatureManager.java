package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

/**
 * Set feature manager into channel.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeFeatureManager implements IPipe {

    /**
     * Feature manager.
     */
    public static final String FEATURE_MANAGER = "featureManager";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        try {
            PipeConfiguration.lookup(channel);
        } catch (NotFoundException e) {
            throw new AbortException(e);
        } catch (InvalidTypeException e) {
            throw new AbortException(e);
        }
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        IFeatureManager features = createFeatureManager();

        features.setConfiguration(PipeConfiguration.lookup(channel));

        channel.add(FEATURE_MANAGER, features);

        return channel;
    }

    /**
     * Creates a feature manager.
     * 
     * @return A manager.
     */
    protected IFeatureManager createFeatureManager() {
        return SpecRunnerServices.get(IFeatureManager.class);
    }

    /**
     * Recover the manager from channel.
     * 
     * @param channel
     *            The channel.
     * @return The feature manager.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static IFeatureManager lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(FEATURE_MANAGER, IFeatureManager.class);
    }
}
