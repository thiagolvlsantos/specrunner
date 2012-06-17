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
package org.specrunner.plugins.impl.flow;

import java.io.IOException;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilIO;
import org.specrunner.util.UtilLog;

/**
 * Allows a pause in execution, waiting for a 'Enter'.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginPause extends AbstractPlugin {

    /**
     * Pause time.
     */
    private Long time;

    /**
     * If set, specify the time to wait. Otherwise, press any key to move on.
     * 
     * @return The time to wait.
     */
    public Long getTime() {
        return time;
    }

    /**
     * Sets the wait time.
     * 
     * @param time
     *            The time.
     */
    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        if (getTime() != null) {
            try {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("(" + Thread.currentThread().getName() + ") sleeping for " + getTime() + "mls.");
                }
                Thread.sleep(getTime());
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("(" + Thread.currentThread().getName() + ") woke up.");
                }
                result.addResult(Success.INSTANCE, context.peek());
            } catch (InterruptedException e) {
                result.addResult(Failure.INSTANCE, context.peek(), e);
            }
        } else {
            try {
                UtilIO.pressKey();
                result.addResult(Success.INSTANCE, context.peek());
            } catch (IOException e) {
                result.addResult(Failure.INSTANCE, context.peek(), e);
            }
        }
        return ENext.DEEP;
    }
}
