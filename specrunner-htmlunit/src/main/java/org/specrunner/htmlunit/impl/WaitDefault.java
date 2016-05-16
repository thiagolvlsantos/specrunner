/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.htmlunit.impl;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.htmlunit.IWait;
import org.specrunner.parameters.core.ParameterHolder;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Wait default implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class WaitDefault extends ParameterHolder implements IWait {

    /**
     * The interval.
     */
    protected Long interval = DEFAULT_INTERVAL;

    /**
     * The max wait time.
     */
    protected Long maxwait = DEFAULT_MAXWAIT;

    @Override
    public void reset() {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("reset()");
        }
        interval = DEFAULT_INTERVAL;
        maxwait = DEFAULT_MAXWAIT;
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_INTERVAL, this);
        fm.set(FEATURE_MAXWAIT, this);
        fm.set(FEATURE_WAIT, this);
    }

    @Override
    public Long getInterval() {
        return interval;
    }

    @Override
    public void setInterval(Long interval) {
        this.interval = interval;
    }

    @Override
    public Long getMaxwait() {
        return maxwait;
    }

    @Override
    public void setMaxwait(Long maxwait) {
        this.maxwait = maxwait;
    }

    @Override
    public boolean isWaitForClient(IContext context, IResultSet result, WebClient client) {
        return true;
    }

    @Override
    public void waitForClient(IContext context, IResultSet result, WebClient client) throws PluginException {
        long time = System.currentTimeMillis();
        int count = client.waitForBackgroundJavaScript(interval);
        while (count > 0 && (System.currentTimeMillis() - time <= maxwait)) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info(count + " threads, waiting for " + interval + "mls on max of " + maxwait + "mls.");
            }
            count = client.waitForBackgroundJavaScript(interval);
        }
    }
}
