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
package org.specrunner.plugins.impl;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.IParameterDecorator;
import org.specrunner.parameters.impl.ParameterDecoratorImpl;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.IParalelPlugin;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.ISleepPlugin;
import org.specrunner.plugins.ITestPlugin;
import org.specrunner.plugins.ITimedPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilLog;

/**
 * Adapter for plugins.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPlugin implements IPlugin, ITestPlugin, ISleepPlugin, ITimedPlugin, IParalelPlugin {

    /**
     * The parameters holder.
     */
    private IParameterDecorator parameters;

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
    private IModel<Object, Boolean> conditionModel;

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
    private IModel<Object, Long> sleepModel;

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
    private IModel<Object, Long> timeoutModel;

    /**
     * Default threadsafe feature.
     */
    public static final String FEATURE_THREADSAFE = AbstractPlugin.class.getName() + ".threadsafe";
    /**
     * Thread safe status. Default is false.
     */
    private Boolean threadsafe;

    /**
     * Default constructor.
     */
    public AbstractPlugin() {
        parameters = new ParameterDecoratorImpl();
        parameters.setDecorated(this);
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("initialize()>" + context.peek());
        }
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        if (condition == null) {
            fh.set(FEATURE_CONDITION, this);
        }
        if (sleep == null) {
            fh.set(FEATURE_SLEEP, this);
        }
        if (timeout == null) {
            fh.set(FEATURE_TIMEOUT, this);
        }
        if (threadsafe == null) {
            fh.set(FEATURE_THREADSAFE, this);
        }
        if (threadsafe == null) {
            threadsafe = false;
        }
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
    public IParameterDecorator getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(IParameterDecorator parameters) {
        this.parameters = parameters;
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
    public IModel<Object, Boolean> getConditionModel() {
        return conditionModel;
    }

    @Override
    public void setConditionModel(IModel<Object, Boolean> conditionModel) {
        this.conditionModel = conditionModel;
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
    public IModel<Object, Long> getSleepModel() {
        return sleepModel;
    }

    @Override
    public void setSleepModel(IModel<Object, Long> sleepModel) {
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
    public IModel<Object, Long> getTimeoutModel() {
        return timeoutModel;
    }

    @Override
    public void setTimeoutModel(IModel<Object, Long> timeoutModel) {
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
     * Set a parameter value.
     * 
     * @param <T>
     *            The plugin type.
     * @param name
     *            The name.
     * @param value
     *            The value.
     * @return The plugin instance.
     * @throws Exception
     *             On setting errors.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractPlugin> T set(String name, Object value) throws Exception {
        parameters.setParameter(name, value);
        return (T) this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}