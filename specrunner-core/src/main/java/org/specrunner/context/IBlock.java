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

import java.util.Map;

import nu.xom.Node;

import org.specrunner.plugins.IPlugin;

/**
 * Block of information on context stack.
 * 
 * @author Thiago Santos
 * 
 */
public interface IBlock {

    /**
     * A valid block has node (<code>hasNode</code>) or plugin (
     * <code>hasPlugin</code>). They cannot be both 'null'.
     * 
     * @return true, if valid, false otherwise.
     */
    boolean isValid();

    /**
     * Indicates if the block node has children.
     * 
     * @return true, if is not terminal in the specification, false, otherwise.
     */
    boolean hasChildren();

    /**
     * Indicates if a given block has changed.
     * 
     * @return true, if the content of block has changed, false, otherwise.
     */
    boolean isChanged();

    /**
     * Sets the changed status.
     * 
     * @param changed
     *            The new status.
     */
    void setChanged(boolean changed);

    /**
     * Indicates if block has been created form a specification or not.
     * 
     * @return True, if it has node attached to it, false, otherwise.
     */
    boolean hasNode();

    /**
     * Returns the specification node object related to the block.
     * 
     * @return The block node.
     */
    Node getNode();

    /**
     * Change block node.
     * 
     * @param node
     *            The new node.
     */
    void setNode(Node node);

    /**
     * Indicates if block has a plugin or not.
     * 
     * @return True, if it has a plugin attached to it, false, otherwise.
     */
    boolean hasPlugin();

    /**
     * The plugin related to the block.
     * 
     * @return The block plugin.
     */
    IPlugin getPlugin();

    /**
     * Sets the block plugin.
     * 
     * @param plugin
     *            The new plugin.
     */
    void setPlugin(IPlugin plugin);

    /**
     * Indicates if block has a mapping or not.
     * 
     * @return True, if it has a map attached to it, false, otherwise.
     */
    boolean hasMap();

    /**
     * Mapping of elements related to the block. This mapping is responsible for
     * the storage of variables and elements that are expected to be shared
     * between plugins.
     * 
     * @return An object mapping.
     */
    Map<String, Object> getMap();

    /**
     * Sets the block mapping of object.
     * 
     * @param map
     *            The new mapping.
     */
    void setMap(Map<String, Object> map);
}
