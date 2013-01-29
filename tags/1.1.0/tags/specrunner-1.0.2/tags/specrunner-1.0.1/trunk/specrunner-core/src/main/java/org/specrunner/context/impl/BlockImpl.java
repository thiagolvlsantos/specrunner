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
package org.specrunner.context.impl;

import java.util.Map;

import nu.xom.Node;

import org.specrunner.context.IBlock;
import org.specrunner.plugins.IPlugin;

/**
 * Default block implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class BlockImpl implements IBlock {

    protected boolean changed;
    protected Node node;
    protected IPlugin plugin;
    protected Map<String, Object> map;

    public BlockImpl(Node node, IPlugin plugin, Map<String, Object> map) {
        this.node = node;
        this.plugin = plugin;
        this.map = map;
    }

    @Override
    public boolean hasChildren() {
        return hasNode() && node.getChildCount() > 0;
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
        return node != null;
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
        return plugin != null;
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
        return map != null;
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
        return node + "," + plugin + "," + map;
    }
}
