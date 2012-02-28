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
package org.specrunner.plugins.impl.flow;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.data.IDataList;
import org.specrunner.plugins.data.IDataMap;
import org.specrunner.plugins.data.IDataTable;
import org.specrunner.plugins.impl.AbstractPluginNamed;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.result.IResultSet;

/**
 * Iterate over a collection.
 * <p>
 * Example: <br>
 * <blockquote> Given the following list:
 * <table border=1>
 * <tr>
 * <th>n</th>
 * <th>square</th>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>9</td>
 * </tr>
 * <tr>
 * <td>5</td>
 * <td>25</td>
 * </tr>
 * <table>
 * <p>
 * Iterate over list and check #{n}^2 = #{square}. </blockquote>
 * 
 * The CSS annotated version would be:
 * 
 * <pre>
 * &lt;table border=1 class="map" name="numbers">
 * &lt;tr>
 * &lt;th>n&lt;/th>
 * &lt;th>square&lt;/th>
 * &lt;/tr>
 * &lt;tr>
 * &lt;td>3&lt;/td>
 * &lt;td>9&lt;/td>
 * &lt;/tr>
 * &lt;tr>
 * &lt;td>5&lt;/td>
 * &lt;td>25&lt;/td>
 * &lt;/tr>
 * &lt;table>
 * &lt;p>
 * Iterate over list and check 
 *      &lt;span class="iterator" name="numbers"&gt;
 *          &lt;span class="compare"&gt; 
 *              &lt;span class="left" value="${Math.pow(n,2)}"&gt;#{n}^2&lt;span&gt; = &lt;span class="right"&gt;#{square}&lt;span&gt; 
 *          &lt;span&gt;
 *      &lt;span&gt;.
 * </pre>
 * 
 * @author Thiago Santos
 * 
 */
public class PluginIterator extends AbstractPluginNamed {

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        ParentNode parent = node.getParent();
        int index = parent.indexOf(node);

        Object list = context.getByName(getName());
        if (list == null) {
            throw new PluginException("Collection named '" + getName() + "' not found.");
        }
        node.detach();

        String local = "item_list";
        String pos = "${index}";
        if (list instanceof IDataTable<?>) {
            local = "item_table";
        } else if (list instanceof IDataMap<?>) {
            local = "item_map";
        }

        Element external = (Element) node.copy();
        external.removeChildren();

        int i = 0;
        for (Object map : (IDataList<?>) list) {
            context.saveLocal(local, map);
            context.saveLocal(pos, "" + i);
            try {
                Node c = node.copy();
                UtilPlugin.setIgnore(c);
                UtilPlugin.performChildren(c, context, result);
                while (c.getChildCount() > 0) {
                    Node ch = c.getChild(0);
                    ((ParentNode) c).removeChild(0);
                    external.appendChild(ch);
                }
            } finally {
                context.clearLocal("" + i);
                context.clearLocal(local);
            }
            i++;
        }
        parent.insertChild(external, index);

        return ENext.SKIP;
    }
}
