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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
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
public class PluginReplacerMap extends AbstractPlugin {

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

    public Nodes replaceMap(String text, Map<String, Node> map) throws PluginException {
        Nodes nodes = new Nodes();
        List<String> names = new LinkedList<String>();
        int pos1 = text.indexOf(UtilEvaluator.START_DATA);
        int pos2 = text.indexOf(UtilEvaluator.END, pos1 + UtilEvaluator.START_DATA.length() + 1);
        while (pos1 >= 0 && pos2 >= pos1) {
            String name = text.substring(pos1, pos2 + 1);
            if (!names.contains(name)) {
                names.add(name);
            }
            pos1 = text.indexOf(UtilEvaluator.START_DATA, pos2 + 1);
            pos2 = text.indexOf(UtilEvaluator.END, pos1 + UtilEvaluator.START_DATA.length() + 1);
        }
        for (String name : names) {
            pos1 = 0;
            pos2 = text.indexOf(name);
            while (pos1 >= 0 && pos2 >= 0) {
                nodes.append(new Text(text.substring(pos1, pos2)));
                if (pos2 > 0 && text.charAt(pos2 - 1) == UtilEvaluator.ESCAPE) {
                    nodes.append(new Text(name));
                } else {
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
                    }
                }
                pos1 = pos2 + name.length();
                pos2 = text.indexOf(name, pos1);
            }
            nodes.append(new Text(text.substring(pos1, text.length())));
        }
        return nodes;
    }
}