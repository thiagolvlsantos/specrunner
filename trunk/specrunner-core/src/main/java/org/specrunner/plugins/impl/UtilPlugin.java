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
package org.specrunner.plugins.impl;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IBlockFactory;
import org.specrunner.context.IContext;
import org.specrunner.parameters.impl.UtilParametrized;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.runner.RunnerException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.util.comparer.IComparator;

/**
 * Plugins utility class.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilPlugin {

    /**
     * Hidden constructor.
     */
    private UtilPlugin() {
    }

    /**
     * Equivalent to <code>create(...,false)</code>.
     * 
     * @param <T>
     *            A subtype of <code>IPlugin</code>.
     * @param context
     *            The reference context.
     * @param type
     *            The plugin type.
     * @param element
     *            The element to be used as reference for plugin creation.
     * @return The newly created plugin.
     * @throws PluginException
     *             On plugin creation errors.
     */
    public static <T extends IPlugin> T create(IContext context, Class<T> type, Element element) throws PluginException {
        return create(context, type, element, false);
    }

    /**
     * Equivalent to <code>create(...,false)</code>.
     * 
     * @param context
     *            The reference context.
     * @param instance
     *            The plugin template instance.
     * @param element
     *            The element to be used as reference for plugin creation.
     * @return The newly created plugin.
     * @throws PluginException
     *             On plugin creation errors.
     */
    public static IPlugin create(IContext context, IPlugin instance, Element element) throws PluginException {
        return create(context, instance, element, false);
    }

    /**
     * Creates a plugin based on its type. The attributes of 'element'will be
     * evaluated as expressions in the given context and used to set plugin
     * attributes with corresponding names.
     * 
     * @param <T>
     *            A subtype of <code>IPlugin</code>.
     * @param context
     *            The reference context.
     * @param type
     *            The plugin type.
     * @param element
     *            The element to be used as reference for plugin creation.
     * @param initialize
     *            true, if initialize() should be called, false, otherwise.
     * @return The newly created plugin.
     * @throws PluginException
     *             On plugin creation errors.
     */
    public static <T extends IPlugin> T create(IContext context, Class<T> type, Element element, boolean initialize) throws PluginException {
        try {
            T result = type.newInstance();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("result ->" + result);
            }
            if (initialize) {
                UtilParametrized.setProperties(context, result, element);
                result.initialize(context);
            }
            return result;
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException("Could not create a plugin for " + type + "." + e.getMessage(), e);
        }
    }

    /**
     * Creates a plugin based on its template. The attributes of 'element' will
     * be evaluated as expressions in the given context and used to set plugin
     * attributes with corresponding names.
     * 
     * @param context
     *            The reference context.
     * @param instance
     *            The template instance.
     * @param element
     *            The element to be used as reference for plugin creation.
     * @param initialize
     *            true, if initialize() should be called, false, otherwise.
     * @return The newly created plugin.
     * @throws PluginException
     *             On plugin creation errors.
     */
    public static IPlugin create(IContext context, IPlugin instance, Element element, boolean initialize) throws PluginException {
        try {
            IPlugin result = instance.copy(context);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("result ->" + result);
            }
            if (initialize) {
                UtilParametrized.setProperties(context, result, element);
                result.initialize(context);
            }
            return result;
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException("Could not create a plugin for " + instance + "." + e.getMessage(), e);
        }
    }

    /**
     * Make plugin sensible to attributes after running.
     * 
     * @param context
     *            The context.
     * @param plugin
     *            The plugin.
     * @param element
     *            The element.
     * @return The plugin itself.
     * @throws PluginException
     *             On plugin finalization.
     */
    public static IPlugin destroy(IContext context, IPlugin plugin, Element element) throws PluginException {
        try {
            if (plugin != PluginNop.emptyPlugin()) {
                UtilParametrized.setProperties(context, plugin, element, false);
            }
            return plugin;
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new PluginException("Could not finalize plugin " + plugin + "." + e.getMessage(), e);
        }
    }

    /**
     * Executes the children of a given node.
     * 
     * @param node
     *            The parent node.
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @throws PluginException
     *             On execution errors.
     */
    public static void performChildren(Node node, IContext context, IResultSet result) throws PluginException {
        for (int i = 0; i < node.getChildCount(); i++) {
            Node t = node.getChild(i);
            try {
                context.getRunner().run(t, context, result);
            } catch (RunnerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new PluginException(e);
            }
        }
    }

    /**
     * Perform a comparison and add the result to the given node.
     * 
     * @param node
     *            Node to be annotated.
     * @param result
     *            The result set.
     * @param expected
     *            The expected String.
     * @param received
     *            The received String
     * @throws PluginException
     *             On plugin errors.
     */
    public static void compare(Node node, IResultSet result, Object expected, Object received) throws PluginException {
        compare(node, null, result, SpecRunnerServices.getComparatorManager().getDefault(), expected, received);
    }

    /**
     * Perform a comparison and add the result to the given node.
     * 
     * @param node
     *            Node to be annotated.
     * @param plugin
     *            The plugin type.
     * @param result
     *            The result set.
     * @param comparator
     *            The comparator.
     * @param expected
     *            The expected String.
     * @param received
     *            The received String
     * @throws PluginException
     *             On plugin errors.
     */
    public static void compare(Node node, IPlugin plugin, IResultSet result, IComparator comparator, Object expected, Object received) throws PluginException {
        if (comparator.match(expected, received)) {
            result.addResult(Success.INSTANCE, SpecRunnerServices.get(IBlockFactory.class).newBlock(node, plugin));
        } else {
            result.addResult(Failure.INSTANCE, SpecRunnerServices.get(IBlockFactory.class).newBlock(node, plugin), new DefaultAlignmentException(String.valueOf(expected), String.valueOf(received)));
        }
    }
}