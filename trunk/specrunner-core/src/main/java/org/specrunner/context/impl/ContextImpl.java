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

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.ContextException;
import org.specrunner.context.IBlock;
import org.specrunner.context.IBlockFactory;
import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.impl.PluginNop;
import org.specrunner.runner.IRunner;
import org.specrunner.source.ISource;
import org.specrunner.util.UtilLog;

/**
 * Default context implementation.
 * 
 * @author Thiago Santos
 */
@SuppressWarnings("serial")
public class ContextImpl extends LinkedList<IBlock> implements IContext {

    /**
     * Parent index.
     */
    private static final int PARENT = 1;
    /**
     * Node index.
     */
    private static final int CURRENT = 0;
    /**
     * Queue of sources.
     */
    protected Deque<ISource> sources = new LinkedList<ISource>();
    /**
     * The runner.
     */
    protected IRunner runner;

    /**
     * Creates a context from a source and a runner.
     * 
     * @param source
     *            The source.
     * @param runner
     *            The runner.
     * @throws ContextException
     *             On creation errors.
     */
    public ContextImpl(ISource source, IRunner runner) throws ContextException {
        sources.add(source);
        add(new BlockImpl(null, PluginNop.emptyPlugin(), new HashMap<String, Object>()));
        this.runner = runner;
    }

    @Override
    public Deque<ISource> getSources() {
        return sources;
    }

    @Override
    public IRunner getRunner() {
        return runner;
    }

    @Override
    public void saveGlobal(String global, Object obj) {
        getLast().getMap().put(global, obj);
    }

    @Override
    public void clearGlobal(String global) {
        getLast().getMap().remove(global);
    }

    @Override
    public void saveLocal(String local, Object obj) {
        IBlock parentBlock = getParentByElement(ParentNode.class);
        if (parentBlock != null) {
            parentBlock.getMap().put(local, obj);
        } else {
            saveGlobal(local, obj);
        }
    }

    @Override
    public void clearLocal(String local) {
        IBlock parentBlock = getParentByElement(ParentNode.class);
        if (parentBlock != null) {
            parentBlock.getMap().remove(local);
        } else {
            clearGlobal(local);
        }
    }

    @Override
    public void saveScoped(String scope, String local, Object obj) {
        for (int i = PARENT; i < size(); i++) {
            IBlock g = get(i);
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace("saveScope = " + g);
            }
            if (g != null) {
                Node node = g.getNode();
                if (node instanceof Element && ((Element) node).getQualifiedName().equals(scope)) {
                    g.getMap().put(local, obj);
                    break;
                }
            }
        }
    }

    @Override
    public void clearScoped(String scope, String local) {
        for (int i = PARENT; i < size(); i++) {
            IBlock g = get(i);
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace("clearScope = " + g);
            }
            if (g != null) {
                Node node = g.getNode();
                if (node instanceof Element && ((Element) node).getQualifiedName().equals(scope)) {
                    g.getMap().remove(local);
                    break;
                }
            }
        }
    }

    @Override
    public IBlock getByElement(Class<? extends Node> type) {
        return findNode(CURRENT, type);
    }

    @Override
    public IBlock getParentByElement(Class<? extends Node> type) {
        return findNode(PARENT, type);
    }

    /**
     * Find a node type.
     * 
     * @param start
     *            The start index.
     * @param type
     *            The type.
     * @return The block found.
     */
    private IBlock findNode(int start, Class<? extends Node> type) {
        for (int i = start; i < size(); i++) {
            IBlock g = get(i);
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace((i != PARENT ? "getByElement" : "getParentByElement") + " = " + g);
            }
            if (g != null && g.getNode() != null && type.isAssignableFrom(g.getNode().getClass())) {
                return g;
            }
        }
        return null;
    }

    @Override
    public IBlock getByPlugin(Class<? extends IPlugin> type) {
        return findPlugin(CURRENT, type);
    }

    @Override
    public IBlock getParentByPlugin(Class<? extends IPlugin> type) {
        return findPlugin(PARENT, type);
    }

    /**
     * Find a plugin by type.
     * 
     * @param start
     *            The start index.
     * @param type
     *            The type.
     * @return The block.
     */
    private IBlock findPlugin(int start, Class<? extends IPlugin> type) {
        for (int i = start; i < size(); i++) {
            IBlock g = get(i);
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace((i != PARENT ? "getByPlugin" : "getParentByPlugin") + " = " + g);
            }
            if (g != null && g.getPlugin() != null && type.isAssignableFrom(g.getPlugin().getClass())) {
                return g;
            }
        }
        return null;
    }

    @Override
    public Object getByName(String name) {
        return findName(CURRENT, name);
    }

    @Override
    public Object getParentByName(String name) {
        return findName(PARENT, name);
    }

    /**
     * Find an object by name.
     * 
     * @param start
     *            The start index.
     * @param name
     *            The object name.
     * @return The object.
     */
    private Object findName(int start, String name) {
        for (int i = start; i < size(); i++) {
            IBlock g = get(i);
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace((i != PARENT ? "getByName" : "getParentByName") + " = " + g);
            }
            if (g != null) {
                Object o = g.getMap().get(name);
                if (o != null) {
                    return o;
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getObjects() {
        Map<String, Object> result = new HashMap<String, Object>();
        for (IBlock b : this) {
            result.putAll(b.getMap());
        }
        return result;
    }

    @Override
    public boolean hasChildren() {
        return peek().hasChildren();
    }

    @Override
    public boolean isChanged() {
        return peek().isChanged();
    }

    @Override
    public void setChanged(boolean changed) {
        peek().setChanged(changed);
    }

    @Override
    public boolean hasNode() {
        return peek().hasNode();
    }

    @Override
    public Node getNode() {
        return peek().getNode();
    }

    @Override
    public void setNode(Node node) {
        setChanged(getNode() != null && getNode() != node);
        peek().setNode(node);
    }

    @Override
    public boolean hasPlugin() {
        return peek().hasPlugin();
    }

    @Override
    public IPlugin getPlugin() {
        return peek().getPlugin();
    }

    @Override
    public void setPlugin(IPlugin plugin) {
        peek().setPlugin(plugin);
    }

    @Override
    public boolean hasMap() {
        return peek().hasMap();
    }

    @Override
    public Map<String, Object> getMap() {
        return peek().getMap();
    }

    @Override
    public void setMap(Map<String, Object> map) {
        peek().setMap(map);
    }

    @Override
    public IBlock newBlock(Node node, IPlugin plugin) {
        return SpecRunnerServices.get(IBlockFactory.class).newBlock(node, plugin);
    }

    @Override
    public IBlock newBlock(Node node, IPlugin plugin, Map<String, Object> map) {
        return SpecRunnerServices.get(IBlockFactory.class).newBlock(node, plugin, map);
    }
}