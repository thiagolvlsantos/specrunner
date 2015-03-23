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
package org.specrunner.runner.core;

import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.listeners.INodeListener;
import org.specrunner.listeners.IPluginListener;
import org.specrunner.listeners.ISourceListener;
import org.specrunner.parameters.core.UtilParametrized;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.ISleepPlugin;
import org.specrunner.plugins.ITestPlugin;
import org.specrunner.plugins.ITimedPlugin;
import org.specrunner.plugins.IWaitPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.PluginNop;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Ignored;
import org.specrunner.result.status.Info;
import org.specrunner.runner.IBlockFilter;
import org.specrunner.runner.IRunner;
import org.specrunner.runner.RunnerException;
import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.collections.ReverseIterable;

/**
 * Default runner implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class RunnerImpl implements IRunner {

    /**
     * A filter instance.
     */
    private IBlockFilter blockFilter;

    @Override
    public IBlockFilter getFilter() {
        return blockFilter;
    }

    @Override
    public void setFilter(IBlockFilter blockFilter) {
        this.blockFilter = blockFilter;
    }

    @Override
    public void run(ISource source, IContext context, IResultSet result) throws RunnerException {
        List<ISourceListener> listeners = SRServices.get(IListenerManager.class).filterByType(ISourceListener.class);
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
            for (ISourceListener sl : new ReverseIterable<ISourceListener>(listeners)) {
                sl.onAfter(source, context, result);
            }
        }
    }

    @Override
    public void run(Node node, IContext context, IResultSet result) throws RunnerException {
        local(node, context, result, null);
    }

    @Override
    public void run(IPlugin plugin, IContext context, IResultSet result) throws RunnerException {
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
    protected void local(final Node node, IContext context, IResultSet result, IPlugin previous) throws RunnerException {
        List<INodeListener> nodeListeners = SRServices.get(IListenerManager.class).filterByType(INodeListener.class);
        ENext doNode = nodeStart(node, context, result, nodeListeners);
        // if listener were used and they said to skip
        if (doNode == ENext.SKIP) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Node listener returned '" + doNode + "'.");
            }
            for (INodeListener nl : new ReverseIterable<INodeListener>(nodeListeners)) {
                nl.onAfter(node, context, result);
            }
            return;
        }
        // new block for node
        final IBlock block = context.newBlock(node, null);
        try {
            // queue block to the context
            context.push(block);

            // create the plugin instance.
            IPluginFactory factory = SRServices.get(IPluginFactory.class);
            IPlugin plugin = previous == null ? factory.newPlugin(node, context) : previous;
            block.setPlugin(plugin);

            if (!block.isValid()) {
                return;
            }

            IBlockFilter local = blockFilter;
            if (local == null) {
                local = BlockFilterDefault.INSTANCE.get();
            }
            if (local != null) {
                local.initialize(context);
                if (!local.accept(block)) {
                    if (local.showMessage(block)) {
                        result.addResult(Ignored.INSTANCE, block, "This block has been disabled.");
                    }
                    return;
                }
            }

            context.addMetadata();

            List<IPluginListener> listeners = SRServices.get(IListenerManager.class).filterByType(IPluginListener.class);

            // initialization
            initialization(context, result, plugin, listeners);

            // conditional execution
            if (checkConditional(plugin, context)) {
                // wait if required
                doWait(plugin, context);

                ENext next = start(context, result, plugin, listeners);
                // if plugin indicates to go deeper in node and node has
                // children.
                if (node != null && next == ENext.DEEP && block.hasChildren()) {
                    // if doStart() has changed the block information, for
                    // example, exchange its node, the deeper must be over
                    // the current node.
                    Node deep = block.isChanged() ? block.getNode() : node;
                    // for each children.
                    for (int i = 0; i < deep.getChildCount(); i++) {
                        Node child = deep.getChild(i);
                        // recursive execution
                        local(child, context, result, null);
                    }
                }
                factory.finalizePlugin(node, context, plugin);
                end(context, result, plugin, listeners);
            } else {
                if (block.hasNode() && node instanceof Element) {
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
            result.addResult(Failure.INSTANCE, block, e);
        } finally {
            // remove block from context
            context.pop();
            // perform after listeners
            for (INodeListener nl : new ReverseIterable<INodeListener>(nodeListeners)) {
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
        ENext doNode = ENext.DEEP;
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
        Node node = context.getNode();
        // perform attribute initialization
        if (node instanceof Element) {
            UtilParametrized.setProperties(context, plugin, (Element) node);
        }
        // perform before initialization
        for (IPluginListener sl : listeners) {
            sl.onBeforeInit(plugin, context, result);
        }
        try {
            // initialize the plugin
            plugin.initialize(context);
        } finally {
            // perform after initialization
            for (IPluginListener sl : new ReverseIterable<IPluginListener>(listeners)) {
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
            IModel<Boolean> model = testPlugin.getConditionModel();
            if (model != null) {
                out = model.getObject(context);
            } else {
                out = testPlugin.getCondition();
            }
        }
        return out == null || out;
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
    protected void doWait(IPlugin plugin, IContext context) throws SpecRunnerException {
        if (plugin != PluginNop.emptyPlugin() && plugin instanceof IWaitPlugin) {
            IWaitPlugin waitPlugin = (IWaitPlugin) plugin;
            IModel<Long> model = waitPlugin.getWaitModel();
            Long sleep = null;
            if (model != null) {
                sleep = model.getObject(context);
            } else {
                sleep = waitPlugin.getWait();
            }
            if (sleep != null) {
                try {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Wait for " + sleep + "mls.");
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

    /**
     * Perform start operation of plugins.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param plugin
     *            The plugin.
     * @param listeners
     *            List of listeners.
     * @return The next step on state machine.
     * @throws SpecRunnerException
     *             On start errors.
     */
    protected ENext start(IContext context, IResultSet result, IPlugin plugin, List<IPluginListener> listeners) throws SpecRunnerException {
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
            for (IPluginListener sl : new ReverseIterable<IPluginListener>(listeners)) {
                sl.onAfterStart(plugin, context, result);
            }
        }
        return next;
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
     *            The start time..
     * @param method
     *            The method name.
     * @throws SpecRunnerException
     *             On conditional evaluation errors.
     */
    protected void checkTimeout(IContext context, IResultSet result, IPlugin plugin, long start, String method) throws SpecRunnerException {
        long total = System.currentTimeMillis() - start;
        if (plugin instanceof ITimedPlugin) {
            ITimedPlugin timedPlugin = (ITimedPlugin) plugin;
            IModel<Long> model = timedPlugin.getTimeoutModel();
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
     * Perform end operations.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param plugin
     *            The plugin.
     * @param listeners
     *            The listeners.
     * @throws SpecRunnerException
     *             On ending errors.
     */
    protected void end(IContext context, IResultSet result, IPlugin plugin, List<IPluginListener> listeners) throws SpecRunnerException {
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
            for (IPluginListener sl : new ReverseIterable<IPluginListener>(listeners)) {
                sl.onAfterEnd(plugin, context, result);
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
        if (plugin != PluginNop.emptyPlugin() && plugin instanceof ISleepPlugin) {
            ISleepPlugin sleepPlugin = (ISleepPlugin) plugin;
            IModel<Long> model = sleepPlugin.getSleepModel();
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