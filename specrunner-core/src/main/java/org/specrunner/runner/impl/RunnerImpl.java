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
package org.specrunner.runner.impl;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.features.IFeatureManager;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.listeners.INodeListener;
import org.specrunner.listeners.IPluginListener;
import org.specrunner.listeners.ISourceListener;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.ISleepPlugin;
import org.specrunner.plugins.ITestPlugin;
import org.specrunner.plugins.ITimedPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.PluginNop;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Ignored;
import org.specrunner.result.status.Info;
import org.specrunner.runner.IRunner;
import org.specrunner.runner.RunnerException;
import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.UtilNode;

/**
 * Default runner implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class RunnerImpl implements IRunner {

    /**
     * List of disabled aliases.
     */
    protected List<String> disabledAliases;
    /**
     * List of enabled aliases.
     */
    protected List<String> enabledAliases;

    @Override
    public void setDisabledAliases(List<String> disabledAliases) {
        if (disabledAliases != null) {
            this.disabledAliases = new LinkedList<String>();
            for (String s : disabledAliases) {
                if (s != null) {
                    this.disabledAliases.add(s.toLowerCase());
                }
            }
        }
    }

    /**
     * Get the list of disabled aliases.
     * 
     * @return Disabled aliases list.
     */
    public List<String> getDisabledAliases() {
        return disabledAliases;
    }

    @Override
    public void setEnabledAliases(List<String> enabledAliases) {
        if (enabledAliases != null) {
            this.enabledAliases = new LinkedList<String>();
            for (String s : enabledAliases) {
                if (s != null) {
                    this.enabledAliases.add(s.toLowerCase());
                }
            }
        }
    }

    /**
     * Get the list of enabled aliases.
     * 
     * @return Enabled aliases list.
     */
    public List<String> getEnabledAliases() {
        return enabledAliases;
    }

    @Override
    public void run(ISource source, IContext context, IResultSet result) throws RunnerException {
        setFeature();
        List<ISourceListener> listeners = SpecRunnerServices.get(IListenerManager.class).filterByType(ISourceListener.class);
        // perform before listeners
        for (ISourceListener sl : listeners) {
            sl.onBefore(source, context, result);
        }
        try {
            // call recursive execution on document root node
            local(source.getDocument(), context, result, null);
        } catch (SourceException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new RunnerException(e);
        } finally {
            // perform after listeners
            for (ISourceListener sl : listeners) {
                sl.onAfter(source, context, result);
            }
        }
    }

    @Override
    public void run(Node node, IContext context, IResultSet result) throws RunnerException {
        setFeature();
        local(node, context, result, null);
    }

    @Override
    public void run(IPlugin plugin, IContext context, IResultSet result) throws RunnerException {
        setFeature();
        if (plugin instanceof IPluginGroup) {
            IPluginGroup group = (IPluginGroup) plugin;
            for (IPlugin p : group.getChildren()) {
                run(p, context, result);
            }
        } else {
            local(null, context, result, plugin);
        }
    }

    /**
     * Set ignored aliases feature.
     */
    protected void setFeature() {
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        disabledAliases = null;
        fm.set(IRunner.FEATURE_DISABLED_ALIASES, this);
        enabledAliases = null;
        fm.set(IRunner.FEATURE_ENABLED_ALIASES, this);
    }

    /**
     * Perform node execution.
     * 
     * @param node
     *            The node.
     * @param context
     *            The current context.
     * @param result
     *            The result.
     * @param previous
     *            The previous plugin.
     * @throws RunnerException
     *             On execution errors.
     */
    protected void local(Node node, IContext context, IResultSet result, IPlugin previous) throws RunnerException {
        List<INodeListener> nListeners = SpecRunnerServices.get(IListenerManager.class).filterByType(INodeListener.class);
        ENext doNode = nodeStart(node, context, result, nListeners);
        // if listener were used and they said to skip
        if (doNode == ENext.SKIP) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Node listener returned '" + doNode + "'.");
            }
            return;
        }
        IPlugin plugin = null;
        IBlock block = null;
        boolean ignored = false;
        try {
            IPluginFactory factory = SpecRunnerServices.get(IPluginFactory.class);
            if (previous == null) {
                // create a plugin based on node information
                plugin = factory.newPlugin(node, context);
            } else {
                plugin = previous;
            }
            // new block for node
            block = context.newBlock(node, plugin);
            if (plugin != PluginNop.emptyPlugin()) {
                String alias = factory.getAlias(plugin.getClass());
                boolean hasDisabled = disabledAliases != null && disabledAliases.contains(alias);
                boolean hasEnabled = enabledAliases != null && !enabledAliases.contains(alias);
                if (alias != null && hasDisabled || hasEnabled) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Plugin '" + alias + "' ignored.");
                    }
                    result.addResult(Ignored.INSTANCE, block, "This plugin has been disabled by our own choice.\n Disabled plugins:" + disabledAliases + ".\n Enabled plugins:" + enabledAliases + ".\n To set disabled or accepted plugins use SpecRunnerServices.get(IFeatureManager.class).add(IRunner.FEATURE_DISABLED_ALIASES,Arrays.asList(<our alias list>)) in a global manner or locally using IConfiguration - IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration().add(IRunner.FEATURE_DISABLED_ALIASES,Arrays.asList(<our alias list>)). The same approach for feature IRunner.FEATURE_ENABLED_ALIASES.");
                    ignored = true;
                    return;
                }
            }

            // queue block to the context
            context.push(block);

            // ----------- METAVARIABLES --------------
            // meta variable 'node'
            context.saveLocal(UtilEvaluator.asVariable("node"), node);
            // meta variable 'plugin'
            context.saveLocal(UtilEvaluator.asVariable("plugin"), plugin);
            // meta variable 'block'
            context.saveLocal(UtilEvaluator.asVariable("block"), block);

            List<IPluginListener> listeners = SpecRunnerServices.get(IListenerManager.class).filterByType(IPluginListener.class);
            // initialization
            initialization(context, result, plugin, listeners);
            // conditional execution
            if (checkConditional(plugin, context)) {
                // perform before start
                for (IPluginListener sl : listeners) {
                    sl.onBeforeStart(plugin, context, result);
                }
                ENext next = null;
                try {
                    long time = System.currentTimeMillis();
                    // perform start
                    next = plugin.doStart(context, result);
                    // check step timeout
                    checkTimeout(context, result, plugin, time, "doStart()");
                } finally {
                    // perform after start
                    for (IPluginListener sl : listeners) {
                        sl.onAfterStart(plugin, context, result);
                    }
                }
                // if plugin indicates to go deeper in node and node has
                // children.
                if (node != null && next == ENext.DEEP && block.hasChildren()) {
                    Node deep = node;
                    // if doStart() has changed the block information, for
                    // example, exchange its node, the deeper must be over
                    // the current node.
                    if (block.isChanged()) {
                        deep = block.getNode();
                    }
                    // for each children.
                    for (int i = 0; i < deep.getChildCount(); i++) {
                        Node child = deep.getChild(i);
                        if (child instanceof Element) {
                            // if not ignored marked
                            if (!UtilNode.isIgnore(child)) {
                                // recursive execution
                                local(child, context, result, null);
                            }
                        } else {
                            // recursive execution
                            local(child, context, result, null);
                        }
                    }
                }
                // perform before end
                for (IPluginListener sl : listeners) {
                    sl.onBeforeEnd(plugin, context, result);
                }
                try {
                    final long time = System.currentTimeMillis();
                    // perform end
                    plugin.doEnd(context, result);
                    // check step timeout
                    checkTimeout(context, result, plugin, time, "doEnd()");
                } finally {
                    // perform after end
                    for (IPluginListener sl : listeners) {
                        sl.onAfterEnd(plugin, context, result);
                    }
                }
            } else {
                if (block.hasNode()) {
                    result.addResult(Info.INSTANCE, block, "Conditional '" + ((Element) node).getAttributeValue("condition") + "' prevented execution.");
                } else {
                    result.addResult(Info.INSTANCE, block, "Conditional prevented execution. " + plugin);
                }
            }
            // sleep if required
            doSleep(plugin, context);
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            // any failure back to specification
            result.addResult(Failure.INSTANCE, context.newBlock(node, plugin), e);
        } finally {
            if (block != null && !ignored) {
                // remove block from context
                context.pop();
            }
            // perform after listeners
            for (INodeListener nl : nListeners) {
                nl.onAfter(node, context, result);
            }
        }
    }

    /**
     * Perform start node listeners.
     * 
     * @param node
     *            The node.
     * @param context
     *            The current context.
     * @param result
     *            The result.
     * @param listeners
     *            Node listeners.
     * @return The next step to be taken, SKIP or go DEEPer.
     */
    protected ENext nodeStart(Node node, IContext context, IResultSet result, List<INodeListener> listeners) {
        // if there are listeners they can guide if runner should go deeper or
        // not
        ENext doNode = listeners.isEmpty() ? ENext.DEEP : ENext.SKIP;
        // perform before listeners
        for (INodeListener nl : listeners) {
            doNode = doNode.max(nl.onBefore(node, context, result));
        }
        return doNode;
    }

    /**
     * Perform initialization.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param plugin
     *            The plugin.
     * @param listeners
     *            Plugin listeners.
     * @throws PluginException
     *             On initialization errors.
     */
    protected void initialization(IContext context, IResultSet result, IPlugin plugin, List<IPluginListener> listeners) throws PluginException {
        // perform before initialization
        for (IPluginListener sl : listeners) {
            sl.onBeforeInit(plugin, context, result);
        }
        try {
            // initialize the plugin
            plugin.initialize(context);
        } finally {
            // perform after initialization
            for (IPluginListener sl : listeners) {
                sl.onAfterInit(plugin, context, result);
            }
        }
    }

    /**
     * Check condition to perform the plugin.
     * 
     * @param plugin
     *            The plugin.
     * @param context
     *            The context.
     * @return true, if should be performed, false, otherwise.
     * @throws SpecRunnerException
     *             On conditional evaluation errors.
     */
    protected boolean checkConditional(IPlugin plugin, IContext context) throws SpecRunnerException {
        Boolean out = null;
        if (plugin instanceof ITestPlugin) {
            ITestPlugin testPlugin = (ITestPlugin) plugin;
            IModel<Object, Boolean> model = testPlugin.getConditionModel();
            if (model != null) {
                out = model.getObject(context);
            } else {
                out = testPlugin.getCondition();
            }
        }
        return out == null || out;
    }

    /**
     * Check timeout to perform the plugin.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param plugin
     *            The plugin.
     * @param start
     *            The plugin.
     * @param method
     *            The method name.
     * @throws SpecRunnerException
     *             On conditional evaluation errors.
     */
    protected void checkTimeout(IContext context, IResultSet result, IPlugin plugin, long start, String method) throws SpecRunnerException {
        long total = System.currentTimeMillis() - start;
        if (plugin instanceof ITimedPlugin) {
            ITimedPlugin timedPlugin = (ITimedPlugin) plugin;
            IModel<Object, Long> model = timedPlugin.getTimeoutModel();
            Long timeout = null;
            if (model != null) {
                timeout = model.getObject(context);
            } else {
                timeout = timedPlugin.getTimeout();
            }
            if (timeout != null && total > timeout) {
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException(method + " has run out of time. time(" + total + ") > timeout(" + timeout + ")"));
            }
        }
    }

    /**
     * Perform a sleep for the step, if specified.
     * 
     * @param plugin
     *            The plugin.
     * @param context
     *            The context.
     * @throws SpecRunnerException
     *             On sleep checking errors.
     */
    protected void doSleep(IPlugin plugin, IContext context) throws SpecRunnerException {
        if (plugin instanceof ISleepPlugin) {
            ISleepPlugin sleepPlugin = (ISleepPlugin) plugin;
            IModel<Object, Long> model = sleepPlugin.getSleepModel();
            Long sleep = null;
            if (model != null) {
                sleep = model.getObject(context);
            } else {
                sleep = sleepPlugin.getSleep();
            }
            if (sleep != null) {
                try {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Sleep for " + sleep + "mls.");
                    }
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                }
            }
        }
    }
}