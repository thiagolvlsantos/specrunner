package org.specrunner.pipeline;

import org.specrunner.util.UtilLog;

/**
 * Pipe profiler.
 * 
 * @author Thiago Santos
 * 
 */
public class ProfilerPipeListener implements IPipeListener {

    /**
     * Time.
     */
    private long time;
    /**
     * Total time.
     */
    private long total;

    @Override
    public void onBeforeCheck(IChannel channel, IPipe source) {
        time = System.currentTimeMillis();
    }

    @Override
    public void onAfterCheck(IChannel channel, IPipe source) {
        if (UtilLog.LOG.isInfoEnabled()) {
            long t = System.currentTimeMillis() - time;
            total += t;
            UtilLog.LOG.info("Channel (" + total + ") on Pipe (" + source.getClass().getSimpleName() + ") check: " + t + " ms");
        }
    }

    @Override
    public void onBeforeProcess(IChannel channel, IPipe source) {
        time = System.currentTimeMillis();
    }

    @Override
    public void onAfterProcess(IChannel channel, IPipe source) {
        if (UtilLog.LOG.isInfoEnabled()) {
            long t = System.currentTimeMillis() - time;
            total += t;
            UtilLog.LOG.info("Channel (" + total + ") on Pipe (" + source.getClass().getSimpleName() + ") process: " + t + " ms");
        }
    }
}