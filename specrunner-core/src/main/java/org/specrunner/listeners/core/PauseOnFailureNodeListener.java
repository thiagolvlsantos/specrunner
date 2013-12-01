/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package org.specrunner.listeners.core;

import java.io.IOException;
import java.util.List;

import nu.xom.Node;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ENext;
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
public class PauseOnFailureNodeListener extends AbstractNodeListener implements ErrorFrameListener {

    /**
     * Enable pause on errors.
     */
    public static final String FEATURE_PAUSE_ON_FAILURE = PauseOnFailureNodeListener.class.getName() + ".pauseOnFailure";
    /**
     * Set true, to pause on errors.
     */
    private Boolean pauseOnFailure = Boolean.FALSE;

    /**
     * Enable error dialog on failures.
     */
    public static final String FEATURE_SHOW_DIALOG = PauseOnFailureNodeListener.class.getName() + ".showDialog";
    /**
     * Set true, to show a dialog.
     */
    private Boolean showDialog = Boolean.FALSE;

    /**
     * Auxiliary frame.
     */
    private ErrorFrame frame;

    /**
     * Flag to skip all dialog messages.
     */
    private Boolean okToAll;

    /**
     * Count to check if errors have been added.
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
    public Boolean getPauseOnFailure() {
        return pauseOnFailure;
    }

    /**
     * Set pause on errors.
     * 
     * @param pauseOnFailure
     *            true, for pause, false, otherwise.
     */
    public void setPauseOnFailure(Boolean pauseOnFailure) {
        this.pauseOnFailure = pauseOnFailure;
    }

    /**
     * The dialog enabled status.
     * 
     * @return true, if dialog is enabled, false, otherwise.
     */
    public Boolean getShowDialog() {
        return showDialog;
    }

    /**
     * Set the dialog status.
     * 
     * @param showDialog
     *            true, to show dialog, false, otherwise.
     */
    public void setShowDialog(Boolean showDialog) {
        this.showDialog = showDialog;
    }

    @Override
    public void reset() {
        pauseOnFailure = false;
        showDialog = false;
        okToAll = false;
    }

    @Override
    public ENext onBefore(Node node, IContext context, IResultSet result) {
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_PAUSE_ON_FAILURE, this);
        fm.set(FEATURE_SHOW_DIALOG, this);
        start = result.size();
        return ENext.DEEP;
    }

    @Override
    public void onAfter(Node node, IContext context, IResultSet result) {
        if (pauseOnFailure && !okToAll) {
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
                    if (showDialog) {
                        showDialog(sb);
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

    /**
     * Show message dialog.
     * 
     * @param sb
     *            The message.
     */
    protected void showDialog(StringBuilder sb) {
        if (frame == null) {
            frame = new ErrorFrame(this);
        }
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Click one of the dialog buttons to move on.");
        }
        frame.setVisible(sb);
    }

    @Override
    public void ok() {
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("'Ok' pressed.");
        }
    }

    @Override
    public void okToAll() {
        okToAll = true;
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("'Ok to All' pressed.");
        }
    }
}
