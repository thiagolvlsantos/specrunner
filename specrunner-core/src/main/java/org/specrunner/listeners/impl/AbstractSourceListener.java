package org.specrunner.listeners.impl;

import org.specrunner.context.IContext;
import org.specrunner.listeners.ISourceListener;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;
import org.specrunner.util.UtilLog;

public abstract class AbstractSourceListener implements ISourceListener {

    @Override
    public void onBefore(ISource source, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onBefore(" + context + "," + result + ")");
        }
    }

    @Override
    public void onAfter(ISource source, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onAfter(" + context + "," + result + ")");
        }
    }
}