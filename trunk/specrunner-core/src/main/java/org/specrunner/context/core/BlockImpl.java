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
package org.specrunner.context.core;

import java.util.Map;

import nu.xom.Node;

import org.specrunner.context.IBlock;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.core.PluginNop;

/**
 * Default block implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class BlockImpl implements IBlock {

    /**
     * Hold if changes have been made to the block.
     */
    protected boolean changed;
    /**
     * The block node.
     */
    protected Node node;
    /**
     * The plugin node.
     */
    protected IPlugin plugin;
    /**
     * The mapping of objects.
     */
    protected Map<String, Object> map;

    /**
     * Creates a block with node, plugin and mapping.
     * 
     * @param node
     *            The node.
     * @param plugin
     *            The plugin.
     * @param map
     *            The mapping.
     */
    public BlockImpl(Node node, IPlugin plugin, Map<String, Object> map) {
        setNode(node);
        setPlugin(plugin);
        setMap(map);
    }

    @Override
    public boolean isValid() {
        return hasNode() || hasPlugin();
    }

    @Override
    public boolean hasChildren() {
        return hasNode() && getNode().getChildCount() > 0;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public boolean hasNode() {
        return getNode() != null;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public boolean hasPlugin() {
        return getPlugin() != null && getPlugin() != PluginNop.emptyPlugin();
    }

    @Override
    public IPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void setPlugin(IPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean hasMap() {
        return getMap() != null;
    }

    @Override
    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return getNode() + "," + getPlugin() + "," + getMap().keySet();
    }
}
