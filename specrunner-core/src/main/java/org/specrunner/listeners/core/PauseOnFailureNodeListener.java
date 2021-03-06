/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.core.flow.PluginPause;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilIO;
import org.specrunner.util.UtilLog;
import org.specrunner.util.output.IOutput;
import org.specrunner.util.output.IOutputFactory;

import nu.xom.Node;

/**
 * Listener to pause execution on errors.
 * 
 * @author Thiago Santos.
 * 
 */
public class PauseOnFailureNodeListener extends AbstractNodeListener implements ErrorFrameListener {

    /**
     * Default conditional feature.
     */
    public static final String FEATURE_CONDITION = PauseOnFailureNodeListener.class.getName() + ".condition";
    /**
     * Pause perform condition.
     */
    protected Boolean condition = Boolean.TRUE;
    /**
     * Enable pause on errors.
     */
    public static final String FEATURE_PAUSE_ON_FAILURE = PauseOnFailureNodeListener.class.getName() + ".pauseOnFailure";
    /**
     * Set true, to pause on errors.
     */
    protected Boolean pauseOnFailure = Boolean.FALSE;

    /**
     * Enable error dialog on failures.
     */
    public static final String FEATURE_SHOW_DIALOG = PauseOnFailureNodeListener.class.getName() + ".showDialog";
    /**
     * Set true, to show a dialog.
     */
    protected Boolean showDialog = Boolean.FALSE;

    /**
     * Modal feature.
     */
    public static final String FEATURE_MODAL = PluginPause.class.getName() + ".modal";
    /**
     * Modal default value.
     */
    public static final Boolean DEFAULT_MODAL = Boolean.TRUE;
    /**
     * Modal flag.
     */
    protected Boolean modal = DEFAULT_MODAL;

    /**
     * Auxiliary frame.
     */
    protected ErrorFrame frame;

    /**
     * Flag to skip all dialog messages.
     */
    protected Boolean okToAll;

    /**
     * Count to check if errors have been added.
     */
    protected int start;

    @Override
    public String getName() {
        return "errorListener";
    }

    /**
     * Condition to enable pause on failure behavior.
     * 
     * @return true, if pause on failure can pause, false, otherwise.
     */
    public Boolean getCondition() {
        return condition;
    }

    /**
     * Set pause condition.
     * 
     * @param condition
     *            true, if pause listener can pause, false, otherwise.
     */
    public void setCondition(Boolean condition) {
        this.condition = condition;
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

    /**
     * Get modal information.
     * 
     * @return true, if modal, false, otherwise.
     */
    public Boolean getModal() {
        return modal;
    }

    /**
     * Set modal flag.
     * 
     * @param modal
     *            Modal mode.
     */
    public void setModal(Boolean modal) {
        this.modal = modal;
    }

    @Override
    public void reset() {
        condition = true;
        pauseOnFailure = false;
        showDialog = false;
        modal = DEFAULT_MODAL;
        okToAll = false;
    }

    @Override
    public ENext onBefore(Node node, IContext context, IResultSet result) {
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_CONDITION, this);
        fm.set(FEATURE_PAUSE_ON_FAILURE, this);
        fm.set(FEATURE_SHOW_DIALOG, this);
        fm.set(FEATURE_MODAL, this);
        start = result.size();
        return ENext.DEEP;
    }

    @Override
    public void onAfter(Node node, IContext context, IResultSet result) {
        if (!condition) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace("Pause on error listener disabled.");
            }
            return;
        }
        if (pauseOnFailure && !okToAll) {
            if (result.countErrors(start) > 0) {
                List<Status> status = result.errorStatus();
                Status[] array = status.toArray(new Status[status.size()]);
                List<IResult> errors = result.filterByStatus(start, result.size(), array);
                try {
                    IOutput output = SRServices.get(IOutputFactory.class).currentOutput();
                    output.println("Pause on error enabled.");
                    StringBuilder sb = new StringBuilder();
                    for (IResult e : errors) {
                        sb.append(e.asString());
                    }
                    output.println("Errors: " + sb);
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
        frame.setVisible(sb, modal);
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
