package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.annotator.AnnotatorException;
import org.specrunner.annotator.IAnnotator;
import org.specrunner.annotator.IAnnotatorFactory;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;

public class PipeAnnotator implements IPipe {

    /**
     * Source.
     */
    public static final String ANNOTATOR = "annotator";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            bind(channel, createAnnotator());
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Creates an annotator instance.
     * 
     * @return A annotator.
     * @throws AnnotatorException
     *             On creation error.
     */
    protected IAnnotator createAnnotator() throws AnnotatorException {
        return SpecRunnerServices.get(IAnnotatorFactory.class).newAnnotator();
    }

    public static void bind(IChannel channel, IAnnotator result) throws NotFoundException, InvalidTypeException {
        channel.add(ANNOTATOR, result);
    }

    public static IAnnotator recover(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(ANNOTATOR, IAnnotator.class);
    }
}
