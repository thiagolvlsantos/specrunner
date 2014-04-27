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
package org.specrunner.sql;

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
 * Split database tables in parts like Hibernate JOINED strategy.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginJoined extends AbstractPlugin {

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
        for (CellAdapter c : table.getCols()) {
            int span = c.hasAttribute("span") ? Integer.parseInt(c.getAttribute("span")) : 1;
            if (colIndex == 0) {
                fixed = span;
                colIndex++;
                columnIndex += span;
                continue;
            }
            TableAdapter copy = UtilNode.newTableAdapter(node.copy());
            String alias = SRServices.get(IPluginFactory.class).getAlias(PluginJoined.class);
            copy.setAttribute(UtilNode.ATT_CSS, table.getAttribute(UtilNode.ATT_CSS).replace(alias, ""));
            if (colIndex > 1) {
                copy.getCaption(0).setValue(c.getAttribute("caption"));
            }
            parent.insertChild(copy.getNode(), index + colIndex);
            copy.select(fixed - 1, colIndex, columnIndex, span);

            colIndex++;
            columnIndex += span;
        }
        UtilNode.setIgnore(node);
        return ENext.SKIP;
    }
}