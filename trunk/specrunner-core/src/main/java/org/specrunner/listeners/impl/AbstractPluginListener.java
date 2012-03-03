package org.specrunner.listeners.impl;

import org.specrunner.context.IContext;
import org.specrunner.listeners.IPluginListener;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;

public abstract class AbstractPluginListener implements IPluginListener {

    @Override
    public void onBeforeInit(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onBeforeInit(" + context + "," + result + ")");
        }
    }

    @Override
    public void onAfterInit(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onAfterInit(" + context + "," + result + ")");
        }
    }

    @Override
    public void onBeforeStart(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onBeforeStart(" + context + "," + result + ")");
        }
    }

    @Override
    public void onAfterStart(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onAfterStart(" + context + "," + result + ")");
        }
    }

    @Override
    public void onBeforeEnd(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onBeforeEnd(" + context + "," + result + ")");
        }
    }

    @Override
    public void onAfterEnd(IPlugin plugin, IContext context, IResultSet result) {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("onAfterEnd(" + context + "," + result + ")");
        }
    }

}
