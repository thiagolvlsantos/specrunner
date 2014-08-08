/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.plugins.core;

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.IParameterDecorator;
import org.specrunner.parameters.core.ParameterHolder;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.IParalelPlugin;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.ISleepPlugin;
import org.specrunner.plugins.ITestPlugin;
import org.specrunner.plugins.ITimedPlugin;
import org.specrunner.plugins.IWaitPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;

/**
 * Adapter for plugins.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPlugin extends ParameterHolder implements IPlugin, ITestPlugin, IWaitPlugin, ISleepPlugin, ITimedPlugin, IParalelPlugin {

    /**
     * The plugin parent.
     */
    private IPluginFactory parent;

    /**
     * Default conditional feature.
     */
    public static final String FEATURE_CONDITION = AbstractPlugin.class.getName() + ".condition";
    /**
     * Perform condition.
     */
    private Boolean condition;
    /**
     * Perform condition model.
     */
    private IModel<Boolean> conditionModel;

    /**
     * Default wait time feature.
     */
    public static final String FEATURE_WAIT = AbstractPlugin.class.getName() + ".wait";
    /**
     * Plugin wait time.
     */
    private Long wait;
    /**
     * Plugin wait time model.
     */
    private IModel<Long> waitModel;

    /**
     * Default sleep time feature.
     */
    public static final String FEATURE_SLEEP = AbstractPlugin.class.getName() + ".sleep";
    /**
     * Plugin sleep time.
     */
    private Long sleep;
    /**
     * Plugin sleep time model.
     */
    private IModel<Long> sleepModel;

    /**
     * Default timeout time feature.
     */
    public static final String FEATURE_TIMEOUT = AbstractPlugin.class.getName() + ".timeout";
    /**
     * Plugin timeout.
     */
    private Long timeout;
    /**
     * Plugin timeout model.
     */
    private IModel<Long> timeoutModel;

    /**
     * Default threadsafe feature.
     */
    public static final String FEATURE_THREADSAFE = AbstractPlugin.class.getName() + ".threadsafe";
    /**
     * Thread safe status. Default is false.
     */
    private Boolean threadsafe;

    /**
     * Feature to set normalized state.
     */
    public static final String FEATURE_NORMALIZED = AbstractPlugin.class.getName() + ".normalized";
    /**
     * The normalized version.
     */
    private Boolean normalized = Boolean.TRUE;

    /**
     * Default constructor.
     */
    public AbstractPlugin() {
    }

    @Override
    public IPluginFactory getParent() {
        return parent;
    }

    @Override
    public void setParent(IPluginFactory parent) {
        this.parent = parent;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("initialize()>" + context.peek());
        }
        IFeatureManager fm = SRServices.getFeatureManager();
        if (condition == null) {
            fm.set(FEATURE_CONDITION, this);
        }
        if (wait == null) {
            fm.set(FEATURE_WAIT, this);
        }
        if (sleep == null) {
            fm.set(FEATURE_SLEEP, this);
        }
        if (timeout == null) {
            fm.set(FEATURE_TIMEOUT, this);
        }
        if (threadsafe == null) {
            fm.set(FEATURE_THREADSAFE, this);
        }
        if (threadsafe == null) {
            threadsafe = false;
        }
        fm.set(FEATURE_NORMALIZED, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("doStart>" + context.peek());
        }
        return ENext.DEEP;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("doEnd>" + context.peek());
        }
    }

    @Override
    public IPlugin copy(IContext context) throws PluginException {
        try {
            IPlugin p = getClass().newInstance();
            IParameterDecorator decorator = p.getParameters();
            PropertyUtils.copyProperties(p, this);
            p.setParameters(decorator);
            return p;
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException(e);
        }
    }

    @Override
    public Boolean getCondition() {
        return condition;
    }

    @Override
    public void setCondition(Boolean condition) {
        this.condition = condition;
    }

    @Override
    public IModel<Boolean> getConditionModel() {
        return conditionModel;
    }

    @Override
    public void setConditionModel(IModel<Boolean> conditionModel) {
        this.conditionModel = conditionModel;
    }

    @Override
    public Long getWait() {
        return wait;
    }

    @Override
    public void setWait(Long wait) {
        this.wait = wait;
    }

    @Override
    public IModel<Long> getWaitModel() {
        return waitModel;
    }

    @Override
    public void setWaitModel(IModel<Long> waitModel) {
        this.waitModel = waitModel;
    }

    @Override
    public Long getSleep() {
        return sleep;
    }

    @Override
    public void setSleep(Long sleep) {
        this.sleep = sleep;
    }

    @Override
    public IModel<Long> getSleepModel() {
        return sleepModel;
    }

    @Override
    public void setSleepModel(IModel<Long> sleepModel) {
        this.sleepModel = sleepModel;
    }

    @Override
    public Long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    @Override
    public IModel<Long> getTimeoutModel() {
        return timeoutModel;
    }

    @Override
    public void setTimeoutModel(IModel<Long> timeoutModel) {
        this.timeoutModel = timeoutModel;
    }

    @Override
    public Boolean getThreadsafe() {
        return threadsafe;
    }

    @Override
    public void setThreadsafe(Boolean threadsafe) {
        this.threadsafe = threadsafe;
    }

    /**
     * If normalized is false, the expected and received String are not compared
     * using their normalized version (trim+remove extra spaces).
     * 
     * @return If normalized is enable or not.
     */
    public Boolean getNormalized() {
        return normalized;
    }

    /**
     * Set the normalized state.
     * 
     * @param normalized
     *            true, to normalize, false, otherwise.
     */
    public void setNormalized(Boolean normalized) {
        this.normalized = normalized;
    }

    /**
     * Get the normalized version of a string.
     * 
     * @param str
     *            The string to be normalized.
     * @return The normalized version of the string, if normalized=true.
     */
    public String getNormalized(String str) {
        if (getNormalized()) {
            return UtilString.normalize(str);
        }
        return str;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}