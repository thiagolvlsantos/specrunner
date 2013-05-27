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
package org.specrunner.plugins;

import nu.xom.Node;

import org.specrunner.context.IContext;

/**
 * Stands for a plugin factory.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPluginFactory {

    /**
     * Given a class, for example, return the corresponding alias for the
     * plugin.
     * 
     * @param type
     *            The plugin type, i.e. 'PluginInclude.class',
     *            'PluginConnection', etc.
     * @return The alias, if type is mapped, null, otherwise. Null does not
     *         means a invalid plugin, but the plugin is either not mapped in
     *         the usual way, or the factory of plugins is not based on alias
     *         premisses.
     */
    String getAlias(Class<? extends IPlugin> type);

    /**
     * Bind a plugin instance to a given type.
     * 
     * @param type
     *            The type.
     * @param alias
     *            The alias.
     * @param plugin
     *            The plugin instance.
     * @return The factory itself.
     * @throws PluginException
     *             On bind errors.
     */
    IPluginFactory bind(String type, String alias, IPlugin plugin) throws PluginException;

    /**
     * Creates a new plugin.
     * 
     * @param source
     *            The source node for plugin creation.
     * @param context
     *            The context.
     * @return A plugin based on source information.
     * @throws PluginException
     *             On creation error.
     */
    IPlugin newPlugin(Node source, IContext context) throws PluginException;

    /**
     * Finalize a plugin.
     * 
     * @param source
     *            The source Node..
     * @param context
     *            The context.
     * @param plugin
     *            The previously created plugin for this node.
     * @return true, if finalized, false, otherwise.
     * @throws PluginException
     *             On finalization error.
     */
    boolean finalizePlugin(Node source, IContext context, IPlugin plugin) throws PluginException;
}
