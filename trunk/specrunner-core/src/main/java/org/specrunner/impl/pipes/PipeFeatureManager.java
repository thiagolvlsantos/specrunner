package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

public class PipeFeatureManager implements IPipe {

    /**
     * Model.
     */
    public static final String FEATURE_MANAGER = "featureManager";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        try {
            PipeConfiguration.recover(channel);
        } catch (NotFoundException e) {
            throw new AbortException(e);
        } catch (InvalidTypeException e) {
            throw new AbortException(e);
        }
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        // feature manager
        IFeatureManager features = createFeatureManager();

        // local feature settings
        features.setConfiguration(PipeConfiguration.recover(channel));

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

    public static IFeatureManager lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(FEATURE_MANAGER, IFeatureManager.class);
    }
}
