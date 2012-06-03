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
package org.specrunner.plugins.impl.text;

import java.util.Map;

import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.IAction;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilEvaluator;

/**
 * Replace mapped elements.
 * 
 * @see org.specrunner.plugins.impl.flow.PluginIterator
 * 
 * @author Thiago Santos
 * 
 */
public class PluginReplacerMap extends AbstractPlugin implements IAction {

    @Override
    @SuppressWarnings("unchecked")
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Object map = context.getByName("item_map");
        if (map == null) {
            return ENext.DEEP;
        }
        Node node = context.getNode();
        Nodes replaced = UtilEvaluator.replaceMap(node.getValue(), (Map<String, Node>) map);
        if (replaced.size() > 1) {
            ParentNode parent = node.getParent();
            int index = parent.indexOf(node);
            parent.removeChild(index);
            for (int i = 0; i < replaced.size(); i++) {
                parent.insertChild(replaced.get(i), index++);
            }
        }
        return ENext.DEEP;
    }
}