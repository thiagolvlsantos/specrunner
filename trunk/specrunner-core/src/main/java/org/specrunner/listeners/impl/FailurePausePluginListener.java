/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
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
 * Listener to pause execution on errors.
 * 
 * @author Thiago Santos.
 * 
 */
public class FailurePausePluginListener extends AbstractPluginListener {

    /**
     * Enable pause on errors.
     */
    public static final String FEATURE_PAUSE_ON_FAILURE = FailurePausePluginListener.class.getName() + ".pauseOnFailure";
    /**
     * Set true, to pause on errors.
     */
    private Boolean pause;

    /**
     * Enable error dialog on failures.
     */
    public static final String FEATURE_SHOW_DIALOG = FailurePausePluginListener.class.getName() + ".showDialog";
    /**
     * Set true, to show a dialog.
     */
    private Boolean dialog;

    /**
     * Auxiliary frame.
     */
    private JFrame frame;

    /**
     * Count to check if errors have be added.
     */
    private int start;

    @Override
    public String getName() {
        return "errorListener";
    }

    /**
     * The pause status.
     * 
     * @return true, for pause on errors, false, otherwise.
     */
    public Boolean getPause() {
        return pause;
    }

    /**
     * Set pause on errors.
     * 
     * @param pause
     *            true, for pause, false, otherwise.
     */
    public void setPause(Boolean pause) {
        this.pause = pause;
    }

    /**
     * The dialog enabled status.
     * 
     * @return true, if dialog is enabled, false, otherwise.
     */
    public Boolean getDialog() {
        return dialog;
    }

    /**
     * Set the dialog status.
     * 
     * @param dialog
     *            true, to show dialog, false, otherwise.
     */
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
