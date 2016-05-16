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
package org.specrunner.context;

import java.util.Deque;
import java.util.Map;

import nu.xom.Node;

import org.specrunner.plugins.IPlugin;
import org.specrunner.runner.IRunner;
import org.specrunner.source.ISource;

/**
 * Stands for the plugin/test runtime environment.
 * 
 * @author Thiago Santos
 * 
 */
public interface IContext extends Deque<IBlock>, IBlock, IBlockFactory {

    /**
     * String to label variable up context access. I.e. <code>_$index</code>,
     * look for first index access, if found, move context down an look for the
     * occurrence.
     */
    String UPACCESS = "_$";

    /**
     * Queue of sources. On file inclusion this queue is changed according and
     * is used to avoid cyclic dependency.
     * 
     * @return The queue.
     */
    Deque<ISource> getSources();

    /**
     * Get current source.
     * 
     * @return The top source.
     */
    ISource getCurrentSource();

    /**
     * The runner related to the context.
     * 
     * @return The runner.
     */
    IRunner getRunner();

    /**
     * Saves a variable with the given name to the global context (outermost
     * block).
     * 
     * @param global
     *            The variable name.
     * @param obj
     *            The value.
     */
    void saveGlobal(String global, Object obj);

    /**
     * Removes a variable name from global context.
     * 
     * @param global
     *            The variable name.
     */
    void clearGlobal(String global);

    /**
     * Saves a variable with the given name to the local context. The local
     * context is defined as the immediate parent of the current node.
     * 
     * @param local
     *            The variable name.
     * @param obj
     *            The value.
     */
    void saveLocal(String local, Object obj);

    /**
     * Removes a variable name from local context.
     * 
     * @param local
     *            The variable name.
     */
    void clearLocal(String local);

    /**
     * Save the value to the current context peek, no matter what type of node
     * it is.
     * 
     * @param name
     *            The name.
     * @param value
     *            The value.
     */
    void saveStrict(String name, Object value);

    /**
     * Clear the context peek mapping from name.
     * 
     * @param name
     *            The name.
     */
    void clearStrict(String name);

    /**
     * Saves a variable to the given scope.
     * 
     * @param scope
     *            Indicates where to bind the value, i.e. when
     *            saveScope("body","name","value") is used in a "span" tag, this
     *            method searches for the "body" backward tag to add variable.
     * @param local
     *            The variable name.
     * @param obj
     *            The value.
     */
    void saveScoped(String scope, String local, Object obj);

    /**
     * Removes a scoped variable.
     * 
     * @param scope
     *            The scope to unbound.
     * @param local
     *            The variable name.
     */
    void clearScoped(String scope, String local);

    /**
     * Search a block by element type.
     * 
     * @param type
     *            The type.
     * @return The nearest block that matches the type.
     */
    IBlock getByElement(Class<? extends Node> type);

    /**
     * Strict parent search by element type.
     * 
     * @param type
     *            The type.
     * @return The nearest parent block that matches the type.
     */
    IBlock getParentByElement(Class<? extends Node> type);

    /**
     * Search a block by plugin type.
     * 
     * @param type
     *            The type.
     * @return The nearest block that matches the type.
     */
    IBlock getByPlugin(Class<? extends IPlugin> type);

    /**
     * Strict parent search by plugin type.
     * 
     * @param type
     *            The type.
     * @return The nearest parent block that matches the type.
     */
    IBlock getParentByPlugin(Class<? extends IPlugin> type);

    /**
     * Check if name is present in context.
     * 
     * @param name
     *            Context.
     * @return true, if present, false, otherwise.
     */
    boolean hasName(String name);

    /**
     * Search a value by its name, from the most restricted block to the
     * outermost (i.e. the root XML element).
     * 
     * @param name
     *            The name.
     * @return The nearest block that matches the type.
     */
    Object getByName(String name);

    /**
     * A strict parent search a value by its name, from the most restricted
     * block to the outermost (i.e. the root XML element).
     * 
     * @param name
     *            The name.
     * @return The nearest block that matches the type.
     */
    Object getParentByName(String name);

    /**
     * A map of all objects in context. Names are resolved from most specific to
     * most general.
     * 
     * @return The naming mapping for this context.
     */
    Map<String, Object> getObjects();

    /**
     * Add metadata from block context.
     */
    void addMetadata();
}
