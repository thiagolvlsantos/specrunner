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
package org.specrunner.plugins.core.logical;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.parameters.IAccess;
import org.specrunner.parameters.IAccessFactory;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.UnstackedPluginException;
import org.specrunner.plugins.core.AbstractPluginTable;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.aligner.core.DefaultAlignmentException;
import org.specrunner.util.string.UtilString;
import org.specrunner.util.xom.UtilNode;
import org.specrunner.util.xom.node.CellAdapter;
import org.specrunner.util.xom.node.RowAdapter;
import org.specrunner.util.xom.node.TableAdapter;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

public class PluginVerifyObjects extends AbstractPluginTable {

    private static final String ATT_TYPE = "type";

    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void doEnd(IContext context, IResultSet result, TableAdapter tableAdapter) throws PluginException {
        try {
            Object value = tableAdapter.getObject(context, false);
            if (!(value instanceof Iterable)) {
                result.addResult(Failure.INSTANCE, context, "'value' attribute should be an instance of iterable.");
            }
            process(context, result, tableAdapter, ((Iterable) value).iterator());
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }

    protected void process(IContext context, IResultSet result, TableAdapter tableAdapter, Iterator iterator) throws Exception {
        String type = tableAdapter.getAttribute(ATT_TYPE);
        if (type == null) {
            List<RowAdapter> rows = tableAdapter.getRows();
            for (int i = 0; i < rows.size(); i++) {
                RowAdapter r = rows.get(i);
                if (UtilNode.isIgnore(r.getNode())) {
                    continue;
                }
                Object received = iterator.next();
                CellAdapter c = r.getCell(0);
                if (UtilNode.isIgnore(c.getNode())) {
                    continue;
                }
                try {
                    Object expected = c.getObject(context, true);
                    if (!c.getComparator().match(expected, received)) {
                        result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new DefaultAlignmentException(String.valueOf(expected), String.valueOf(received)));
                    } else {
                        result.addResult(Success.INSTANCE, context.newBlock(c.getNode(), this));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return;
        }
        List<RowAdapter> rows = tableAdapter.getRows();
        RowAdapter header = rows.get(0);
        for (int i = 1; i < rows.size(); i++) {
            RowAdapter r = rows.get(i);
            if (UtilNode.isIgnore(r.getNode())) {
                continue;
            }
            if (!iterator.hasNext()) {
                for (int j = i; j < rows.size(); j++) {
                    r = rows.get(j);
                    if (UtilNode.isIgnore(r.getNode())) {
                        continue;
                    }
                    result.addResult(Failure.INSTANCE, context.newBlock(r.getCell(0).getNode(), this), new UnstackedPluginException("Missing row."));
                }
                break;
            }
            Object current = iterator.next();
            for (int j = 0; j < header.getCellsCount(); j++) {
                CellAdapter h = header.getCell(j);
                if (UtilNode.isIgnore(h.getNode())) {
                    continue;
                }
                CellAdapter c = r.getCell(j);
                if (UtilNode.isIgnore(c.getNode())) {
                    continue;
                }
                String str = h.getValue(context);
                String att = h.getAttribute("feature", h.getAttribute("field", h.getAttribute("property", str)));
                String field = UtilString.getNormalizer().camelCase(att);
                IAccess access = SRServices.get(IAccessFactory.class).newAccess(current, field);
                Class<?> attType = access.expected(current, field);
                Object received = access.get(current, field);
                if (attType.isArray() || Collection.class.isAssignableFrom(attType)) {
                    Nodes nodes = c.getNode().query("child::table");
                    if (nodes.size() == 0) {
                        if (received != null) {
                            result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new Exception("Expected @null, received:" + received));
                        } else {
                            result.addResult(Success.INSTANCE, context.newBlock(c.getNode(), this));
                        }
                        continue;
                    } else {
                        Node node = nodes.get(0);
                        TableAdapter subtable = new TableAdapter((Element) node);
                        if (h.hasAttribute(ATT_TYPE)) {
                            subtable.setAttribute(ATT_TYPE, h.getAttribute(ATT_TYPE));
                        }
                        if (attType.isArray()) {
                            List<Object> collection = new LinkedList<Object>();
                            for (int k = 0; k < Integer.MAX_VALUE; k++) {
                                try {
                                    collection.add(Array.get(received, k));
                                } catch (ArrayIndexOutOfBoundsException e) { // ended
                                    break;
                                }
                            }
                            if (subtable.getRowCount() == 0) {
                                if (received == null || !collection.isEmpty()) {
                                    result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new Exception("Expected @empty, received:" + received));
                                } else {
                                    result.addResult(Success.INSTANCE, context.newBlock(c.getNode(), this));
                                }
                            } else {
                                process(context, result, subtable, collection.iterator());
                            }
                        } else {
                            if (subtable.getRowCount() == 0) {
                                if (received == null || !((Collection) received).isEmpty()) {
                                    result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new Exception("Expected @empty, received:" + received));
                                } else {
                                    result.addResult(Success.INSTANCE, context.newBlock(c.getNode(), this));
                                }
                            } else {
                                process(context, result, subtable, ((Collection) received).iterator());
                            }
                        }
                    }
                } else {
                    Object expected = c.getObject(context, true);
                    if (!c.getComparator(h.getComparator()).match(expected, received)) {
                        result.addResult(Failure.INSTANCE, context.newBlock(c.getNode(), this), new DefaultAlignmentException(String.valueOf(expected), String.valueOf(received)));
                    } else {
                        result.addResult(Success.INSTANCE, context.newBlock(c.getNode(), this));
                    }
                }
            }
        }
        if (iterator.hasNext()) {
            int count = 0;
            Element tr = new Element("tr");
            int colspan = 1;
            if (tableAdapter.getRowCount() > 0) {
                colspan = tableAdapter.getRow(0).getCellsCount();
            }
            while (iterator.hasNext()) {
                count++;
                Object current = iterator.next();
                Element td = new Element("td");
                td.addAttribute(new Attribute("colspan", String.valueOf(colspan)));
                tr.appendChild(td);
                td.appendChild(String.valueOf(current));
            }
            tableAdapter.append(tr);
            result.addResult(Failure.INSTANCE, context.newBlock(tableAdapter.getNode(), this), new UnstackedPluginException("Collection has '" + count + "' unexpected " + (count == 1 ? "item" : "items") + "."));
        }
    }
}