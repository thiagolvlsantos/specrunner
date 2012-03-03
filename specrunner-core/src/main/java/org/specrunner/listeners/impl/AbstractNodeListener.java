package org.specrunner.listeners.impl;

import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.listeners.INodeListener;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;

public abstract class AbstractNodeListener implements INodeListener {

    @Override
    public void onBefore(Node node, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onBefore(" + context + "," + result + ")");
        }
    }

    @Override
    public void onAfter(Node node, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onAfter(" + context + "," + result + ")");
        }
    }
}