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
    protected long time;
    /**
     * Total time.
     */
    protected long total;

    @Override
    public void onBeforeCheck(IChannel channel, IPipe source) {
        time = System.currentTimeMillis();
    }

    @Override
    public void onAfterCheck(IChannel channel, IPipe source) {
        if (UtilLog.LOG.isDebugEnabled()) {
            long t = System.currentTimeMillis() - time;
            total += t;
            UtilLog.LOG.debug("Channel (" + total + ") on Pipe (" + source.getClass().getSimpleName() + ") check: " + t + " ms");
        }
    }

    @Override
    public void onBeforeProcess(IChannel channel, IPipe source) {
        time = System.currentTimeMillis();
    }

    @Override
    public void onAfterProcess(IChannel channel, IPipe source) {
        if (UtilLog.LOG.isDebugEnabled()) {
            long t = System.currentTimeMillis() - time;
            total += t;
            UtilLog.LOG.debug("Channel (" + total + ") on Pipe (" + source.getClass().getSimpleName() + ") process: " + t + " ms");
        }
    }
}