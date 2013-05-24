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
package org.specrunner.context.impl;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Node;

import org.specrunner.context.IBlock;
import org.specrunner.context.IBlockFactory;
import org.specrunner.plugins.IPlugin;
import org.specrunner.util.UtilLog;

/**
 * Default block factory implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class BlockFactoryImpl implements IBlockFactory {

    @Override
    public IBlock newBlock(Node element, IPlugin plugin) {
        check(element, plugin);
        return newBlock(element, plugin, new HashMap<String, Object>());
    }

    /**
     * Check block items.
     * 
     * @param element
     *            The element.
     * @param plugin
     *            The plugin.
     */
    protected void check(Node element, IPlugin plugin) {
        if (element == null && plugin == null) {
            RuntimeException re = new IllegalArgumentException();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(re.getMessage(), re);
            }
            throw re;
        }
    }

    @Override
    public IBlock newBlock(Node element, IPlugin plugin, Map<String, Object> map) {
        check(element, plugin);
        return new BlockImpl(element, plugin, map);
    }
}