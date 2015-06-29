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
package org.specrunner.context.core;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Node;

import org.specrunner.context.IBlock;
import org.specrunner.context.IBlockFactory;
import org.specrunner.plugins.IPlugin;

/**
 * Default block factory implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class BlockFactoryImpl implements IBlockFactory {

    @Override
    public IBlock newBlock(Node element, IPlugin plugin) {
        return newBlock(element, plugin, new HashMap<String, Object>());
    }

    @Override
    public IBlock newBlock(Node element, IPlugin plugin, Map<String, Object> map) {
        return new BlockImpl(element, plugin, map);
    }
}