package org.specrunner.plugins.core.flow;

import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginTable;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.xom.UtilNode;
import org.specrunner.util.xom.node.CellAdapter;
import org.specrunner.util.xom.node.RowAdapter;
import org.specrunner.util.xom.node.TableAdapter;

import nu.xom.Attribute;
import nu.xom.Element;

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

/**
 * Replicate headers of table to lines, repeating behavior expected by header.
 * 
 * @author Thiago Santos
 *
 */
public class PluginRepeat extends AbstractPluginTable {

    @Override
    public ActionType getActionType() {
        return Undefined.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result, TableAdapter table) throws PluginException {
        List<RowAdapter> rows = table.getRows();
        if (rows.isEmpty()) {
            return ENext.SKIP; // ignore
        }
        RowAdapter header = rows.get(0);
        Element node = (Element) header.getNode();
        List<CellAdapter> headers = header.getCells();
        for (int i = 1; i < rows.size(); i++) {
            RowAdapter row = rows.get(i);
            // replace header tr
            for (int j = 0; j < node.getAttributeCount(); j++) {
                Attribute att = node.getAttribute(j);
                String localName = att.getLocalName();
                if (!row.hasAttribute(localName)) {
                    row.setAttribute(localName, node.getAttributeValue(localName));
                } else {
                    if (UtilNode.ATT_CSS.equals(localName)) {
                        row.setAttribute(localName, row.getAttribute(localName) + " " + node.getAttributeValue(localName));
                    }
                }
            }
            List<CellAdapter> values = row.getCells();
            if (headers.size() != values.size()) {
                result.addResult(Failure.INSTANCE, context.newBlock(row.getNode(), this), "Number of headers(" + headers.size() + ") is different of values(" + values.size() + ").");
            } else {
                for (int j = 0; j < headers.size(); j++) {
                    CellAdapter cellTh = headers.get(j);
                    Element th = (Element) cellTh.getNode();
                    CellAdapter cellTd = values.get(j);
                    for (int k = 0; k < th.getAttributeCount(); k++) {
                        Attribute att = th.getAttribute(k);
                        String localName = att.getLocalName();
                        if (!cellTd.hasAttribute(localName)) {
                            cellTd.setAttribute(localName, th.getAttributeValue(localName));
                        } else {
                            if (UtilNode.ATT_CSS.equals(localName)) {
                                cellTd.setAttribute(localName, cellTd.getAttribute(localName) + " " + th.getAttributeValue(localName));
                            }
                        }
                    }
                }
            }
        }
        UtilNode.setIgnore(node);
        return ENext.DEEP;
    }
}
