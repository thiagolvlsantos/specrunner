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
package org.specrunner.sql;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.TableAdapter;
import org.specrunner.util.xom.UtilNode;

/**
 * Split database tables in parts like Hibernate JOINED strategy. The order of
 * database inserts is from left to right, and the start columns defined by
 * first &lt;col&gt; attribute span will be repeated in each expansion.
 * 
 * The result is that a table became various, and the original table is ignored
 * in execution.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginJoined extends AbstractPlugin {

    /**
     * Constant for &lt;col&gt; span attribute.
     */
    public static final String ATTR_SPAN = "span";
    /**
     * Constant for &lt;col&gt; caption attribute.
     */
    public static final String ATTR_CAPTION = "caption";

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Node node = context.getNode();
        ParentNode parent = node.getParent();
        int index = parent.indexOf(node);
        TableAdapter table = UtilNode.newTableAdapter(node);
        int colIndex = 0;
        int fixed = 0;
        int columnIndex = 0;
        Element quote = new Element("blockquote");
        Element included = new Element("table");
        quote.appendChild(included);
        Element tr = new Element("tr");
        included.appendChild(tr);
        Element td = new Element("td");
        tr.appendChild(td);
        UtilNode.appendCss(included, "included");
        for (CellAdapter c : table.getCols()) {
            int span = Integer.parseInt(c.getAttribute(ATTR_SPAN, "1"));
            if (colIndex == 0) {
                if (span < 2) {
                    throw new PluginException("First col must specify the number of fixed columns, which should be greater than 1, one column for Action type and the others like ID should be repeated.");
                }
                fixed = span;
                colIndex++;
                columnIndex += span;
                continue;
            }
            TableAdapter copy = UtilNode.newTableAdapter(node.copy());
            String alias = SRServices.get(IPluginFactory.class).getAlias(PluginJoined.class);
            copy.setAttribute(UtilNode.ATT_CSS, table.getAttribute(UtilNode.ATT_CSS).replace(alias, ""));
            if (colIndex < table.getColsCount() - 1) {
                if (!c.hasAttribute(ATTR_CAPTION)) {
                    throw new PluginException("Colgroup item (" + colIndex + ") '" + c.toXML() + "' missing caption attribute. Caption is used to define the expanded table column name. i.e. caption=\"Customers\"");
                }
            }
            if (c.hasAttribute(ATTR_CAPTION)) {
                copy.getCaption(0).setValue(c.getAttribute(ATTR_CAPTION));
            }
            td.appendChild(copy.getNode());
            copy.select(fixed - 1, colIndex, columnIndex, span);
            colIndex++;
            columnIndex += span;
        }
        parent.insertChild(quote, index + 1);
        UtilNode.setIgnore(node);
        return ENext.SKIP;
    }
}