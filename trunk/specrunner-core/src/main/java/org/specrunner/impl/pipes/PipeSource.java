package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.source.resource.ResourceException;

public class PipeSource implements IPipe {

    /**
     * Source.
     */
    public static final String SOURCE = "source";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            // create the source
            ISource source = createSource(PipeInput.recover(channel));

            // adding default resources
            addResources(source);

            bind(channel, source);
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Creates a source from input.
     * 
     * @param input
     *            The input name.
     * @return The source instance.
     * @throws SourceException
     *             On source creation errors.
     */
    protected ISource createSource(String input) throws SourceException {
        return SpecRunnerServices.get(ISourceFactory.class).newSource(input);
    }

    /**
     * Add resources to a source.
     * 
     * @param source
     *            The source.
     * @throws ResourceException
     *             On resource errors.
     */
    protected void addResources(ISource source) throws ResourceException {
        IResourceManager manager = source.getManager();
        manager.addDefaultCss();
        manager.addDefaultJs();
    }

    public static void bind(IChannel channel, ISource source) throws NotFoundException, InvalidTypeException {
        channel.add(SOURCE, source);
    }

    public static ISource recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(SOURCE, ISource.class);
    }
}
