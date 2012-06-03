package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerServices;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.transformer.ITransformer;

public class PipeTransformSource implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            // performs source transformation.
            PipeSource.bind(channel, transformSource(PipeSource.recover(channel)));
        } catch (SourceException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Perform a transformation of a given source.
     * 
     * @param source
     *            The source to be transformed.
     * @return The transformed source.
     * @throws SourceException
     *             On transformation errors.
     */
    protected ISource transformSource(ISource source) throws SourceException {
        return SpecRunnerServices.get(ITransformer.class).transform(source);
    }
}