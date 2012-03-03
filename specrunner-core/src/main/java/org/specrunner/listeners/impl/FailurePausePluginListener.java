package org.specrunner.listeners.impl;

import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilIO;
import org.specrunner.util.UtilLog;

/**
 * Pause execution on errors.
 * 
 * @author Thiago Santos.
 * 
 */
public class FailurePausePluginListener extends AbstractPluginListener {

    public static final String FEATURE_PAUSE_ON_FAILURE = FailurePausePluginListener.class.getName() + ".pauseOnFailure";
    private Boolean pause;

    public static final String FEATURE_SHOW_DIALOG = FailurePausePluginListener.class.getName() + ".showDialog";
    private Boolean dialog;

    private JFrame frame;

    private int start;

    @Override
    public String getName() {
        return "errorListener";
    }

    public Boolean getPause() {
        return pause;
    }

    public void setPause(Boolean pause) {
        this.pause = pause;
    }

    public Boolean getDialog() {
        return dialog;
    }

    public void setDialog(Boolean dialog) {
        this.dialog = dialog;
    }

    @Override
    public void reset() {
        pause = false;
        dialog = false;
    }

    @Override
    public void onBeforeInit(IPlugin plugin, IContext context, IResultSet result) {
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        try {
            fm.set(FEATURE_PAUSE_ON_FAILURE, "pause", Boolean.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        try {
            fm.set(FEATURE_SHOW_DIALOG, "dialog", Boolean.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        start = result.size();
    }

    @Override
    public void onAfterEnd(IPlugin plugin, IContext context, IResultSet result) {
        if (pause) {
            List<Status> status = result.errorStatus();
            Status[] array = status.toArray(new Status[status.size()]);
            List<IResult> errors = result.filterByStatus(start, result.size(), array);
            if (!errors.isEmpty()) {
                try {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Error pause enabled.");
                    }
                    StringBuilder sb = new StringBuilder();
                    for (IResult e : errors) {
                        sb.append(e.asString());
                    }
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Errors:" + sb);
                    }
                    if (dialog) {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Click OK on dialog.");
                        }
                        if (frame == null) {
                            frame = new JFrame("Error report");
                        }
                        frame.setVisible(true);
                        JOptionPane.showMessageDialog(frame, sb);
                        frame.setVisible(false);
                    } else {
                        UtilIO.pressKey();
                    }
                } catch (IOException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                }
            }
        }
    }
}
