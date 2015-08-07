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
package org.specrunner.plugins.core.text;

import java.util.Map;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPlugin;
import org.specrunner.plugins.core.data.PluginMap;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;

import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

/**
 * Replace mapped elements.
 * 
 * @see org.specrunner.plugins.core.flow.PluginIterator
 * 
 * @author Thiago Santos
 * 
 */
public class PluginReplacerMap extends AbstractPlugin {
    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Object map = context.getByName("item_map");
        if (map == null) {
            return ENext.DEEP;
        }
        Node node = context.getNode();
        Nodes replaced = replaceMap(node.getValue(), (Map<String, Node>) map);
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

    /**
     * Replaces text with corresponding values in map.
     * 
     * @param text
     *            The text to be replace.
     * @param map
     *            The map of values to be replace.
     * @return The nodes which represents the replaced text.
     * @throws PluginException
     *             On replacement errors.
     */
    public Nodes replaceMap(String text, Map<String, Node> map) throws PluginException {
        Nodes nodes = new Nodes();
        int pos1 = 0;
        int pos2 = text.indexOf(PluginMap.START_DATA);
        int pos3 = text.indexOf(PluginMap.END_DATA, pos2 + PluginMap.START_DATA.length() + 1);
        while (pos2 >= 0 & pos3 > pos2) {
            nodes.append(new Text(text.substring(pos1, pos2)));
            String name = text.substring(pos2, pos3 + 1);
            Node n = map.get(name);
            if (n != null) {
                if (n instanceof ParentNode) {
                    ParentNode pn = (ParentNode) n;
                    for (int i = 0; i < pn.getChildCount(); i++) {
                        nodes.append(pn.getChild(i).copy());
                    }
                } else {
                    nodes.append(n.copy());
                }
            } else {
                nodes.append(new Text(text.substring(pos2, pos3)));
            }
            pos1 = pos3 + 1;
            pos2 = text.indexOf(PluginMap.START_DATA, pos1);
            pos3 = text.indexOf(PluginMap.END_DATA, pos2 + PluginMap.START_DATA.length() + 1);
        }
        if (pos1 != text.length() + 1) {
            nodes.append(new Text(text.substring(pos1, text.length())));
        }
        return nodes;
    }
}
