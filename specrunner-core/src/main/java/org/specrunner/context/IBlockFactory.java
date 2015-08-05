/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
 * Block factory.
 * 
 * @author Thiago Santos
 * 
 */
public interface IBlockFactory {

    /**
     * Creates a new block.
     * 
     * @param node
     *            A node.
     * @param plugin
     *            A plugin.
     * @return A new block.
     */
    IBlock newBlock(Node node, IPlugin plugin);

    /**
     * Create a new block.
     * 
     * @param node
     *            A node.
     * @param plugin
     *            A plugin.
     * @param map
     *            An object mapping.
     * @return A new block.
     */
    IBlock newBlock(Node node, IPlugin plugin, Map<String, Object> map);
}
