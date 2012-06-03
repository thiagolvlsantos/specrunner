package org.specrunner.impl.pipes;

import java.io.File;
import java.util.Map;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.result.IResultSet;

public class PipeDump implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            // message before dump
            messageBefore(PipeInput.recover(channel));

            // dump results
            IResultSet result = PipeResult.recover(channel);

            Map<String, Object> model = PipeModel.recover(channel);
            PipeDumper.recover(channel).dump(PipeSource.recover(channel), result, model);

            // message after dump
            messageAfter(model, result);
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Print message before execution.
     * 
     * @param input
     *            The input.
     */
    protected void messageBefore(String input) {
        System.out.println();
        System.out.println(" Input " + getNome() + ": " + (input != null ? input.replace('/', File.separatorChar) : "null"));
    }

    /**
     * Thread normalized name.
     * 
     * @return The normalized thread named.
     */
    private String getNome() {
        return "(" + SpecRunnerServices.get(IConcurrentMapping.class).getThread() + ")";
    }

    /**
     * Message after execution.
     * 
     * @param info
     *            The model information.
     * @param result
     *            The result set.
     */
    protected void messageAfter(Map<String, Object> info, IResultSet result) {
        System.out.printf("Result " + getNome() + ": %s \n", result.asString());
        System.out.printf("    In " + getNome() + ": %d mls \n", info.get(PipeTime.TIME));
        System.out.printf("    At " + getNome() + ": %s \n", info.get(PipeTimestamp.DATE));
    }
}
