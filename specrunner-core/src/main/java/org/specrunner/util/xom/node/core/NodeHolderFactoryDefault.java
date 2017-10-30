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
package org.specrunner.util.xom.node.core;

import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.INodeHolderFactory;

import nu.xom.Node;

/**
 * Default implementation of a node holder creator.
 * 
 * @author Thiago Santos
 * 
 */
public class NodeHolderFactoryDefault implements INodeHolderFactory {

    /**
     * Creates a node holder from a node.
     * 
     * @param node
     *            A node holder.
     * @return A holder.
     */
    public INodeHolder newHolder(Node node) {
        return new NodeHolderDefault(node);
    }
}
