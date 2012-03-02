package org.specrunner.listeners.impl;

import java.io.IOException;
import java.util.List;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilIO;
import org.specrunner.util.UtilLog;

public class FailurePausePluginListener extends AbstractPluginListener {

    public static final String FEATURE_PAUSE_ON_FAILURE = FailurePausePluginListener.class.getName() + ".pauseOnFailure";
    private Boolean pause;
    private int start;

    @Override
    public String getName() {
        return "errorListener";
    }

    @Override
    public IContext getContext() {
        return null;
    }

    public Boolean getPause() {
        return pause;
    }

    public void setPause(Boolean pause) {
        this.pause = pause;
    }

    @Override
    public void onBeforeInit(IContext context, IResultSet result) {
        pause = false;
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        try {
            fm.set(FEATURE_PAUSE_ON_FAILURE, "pause", Boolean.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        start = result.size();
    }

    @Override
    public void onAfterEnd(IContext context, IResultSet result) {
        if (pause) {
            List<Status> status = result.errorStatus();
            Status[] array = status.toArray(new Status[status.size()]);
            List<IResult> errors = result.filterByStatus(start, result.size(), array);
            if (!errors.isEmpty()) {
                try {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Error pause enabled.");
                    }
                    for (IResult e : errors) {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Error:" + e.asString());
                        }
                    }
                    UtilIO.pressKey();
                } catch (IOException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
