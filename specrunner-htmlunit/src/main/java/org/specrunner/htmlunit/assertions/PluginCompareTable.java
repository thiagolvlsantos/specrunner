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
package org.specrunner.htmlunit.assertions;

import java.util.Iterator;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.htmlunit.AbstractPluginFindSingle;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.UtilPlugin;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.result.status.Failure;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.CellAdapter;
import org.specrunner.util.xom.INodeHolder;
import org.specrunner.util.xom.RowAdapter;
import org.specrunner.util.xom.TableAdapter;
import org.specrunner.util.xom.UtilNode;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

/**
 * Compare tables.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCompareTable extends AbstractPluginFindSingle {

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement element) throws PluginException {
        Node node = context.getNode();
        boolean success = compareTable(context, result, page, element, node);
        if (!success) {
            result.addResult(Failure.INSTANCE, context.newBlock(node, this), new PluginException("Tables do not match."), SRServices.get(IWritableFactoryManager.class).get(Page.class).newWritable(page));
        }
    }

    /**
     * Compare a table.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param page
     *            The client page.
     * @param element
     *            The element.
     * @param node
     *            The node.
     * @return The result of comparison.
     * @throws PluginException
     *             On evaluation error.
     */
    protected boolean compareTable(IContext context, IResultSet result, SgmlPage page, HtmlElement element, Node node) throws PluginException {
        boolean success = true;

        Nodes tables = node.query("descendant-or-self::table");
        Node table = UtilNode.getHighest(tables);
        if (table == null) {
            result.addResult(Failure.INSTANCE, context.newBlock(node, this), new PluginException("Table to be compared is not specified."));
            return false;
        }
        TableAdapter tableExpected = UtilNode.newTableAdapter(table);

        List<?> tablesReceived = element.getByXPath("descendant-or-self::table");
        if (tablesReceived.isEmpty()) {
            result.addResult(Failure.INSTANCE, context.newBlock(table, this), new PluginException("Expected table not present in input."));
            return false;
        }
        HtmlTable tableReceived = (HtmlTable) tablesReceived.get(0);

        List<?> tableCaptions = tableReceived.getByXPath("child::caption");
        Iterator<?> iteCaptions = tableCaptions.iterator();
        for (CellAdapter expected : tableExpected.getCaptions()) {
            if (!iteCaptions.hasNext()) {
                result.addResult(Failure.INSTANCE, context.newBlock(expected.getNode(), this), new PluginException("Caption cells missing in received table."));
                success = false;
                continue;
            }
            HtmlElement received = (HtmlElement) iteCaptions.next();
            while (!received.isDisplayed() && iteCaptions.hasNext()) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Ignore table invisible caption:" + received.asXml());
                }
                received = (HtmlElement) iteCaptions.next();
            }
            if (expected.hasAttribute(UtilNode.IGNORE) && received.isDisplayed()) {
                success = success & compareTerminal(this, context, result, page, expected, received);
            }
        }
        // lookup for visible elements
        boolean visible = false;
        HtmlElement tmp;
        while (!visible && iteCaptions.hasNext()) {
            tmp = (HtmlElement) iteCaptions.next();
            visible = tmp.isDisplayed();
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Check visibility(" + visible + ") for " + tmp.asXml());
            }
        }
        if (visible) {
            result.addResult(Failure.INSTANCE, context.newBlock(table, this), new PluginException("Received table has more captions than expected."));
            success = false;
        }

        List<?> tableElements = tableReceived.getByXPath("child::tr/th | child::tr/td | child::thead/tr/th | child::thead/tr/td | child::tbody/tr/th | child::tbody/tr/td");
        Iterator<?> iteElements = tableElements.iterator();
        for (RowAdapter rowsExpected : tableExpected.getRows()) {
            for (CellAdapter expected : rowsExpected.getCells()) {
                if (!iteElements.hasNext()) {
                    result.addResult(Failure.INSTANCE, context.newBlock(expected.getNode(), this), new PluginException("Cell missing in received table."));
                    success = false;
                    continue;
                }
                HtmlElement received = (HtmlElement) iteElements.next();
                while (!received.isDisplayed() && iteElements.hasNext()) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Ignore table invisible element:" + received.asXml());
                    }
                    received = (HtmlElement) iteElements.next();
                }
                if (!expected.hasAttribute(UtilNode.IGNORE) && received.isDisplayed()) {
                    success = success & compareTerminal(this, context, result, page, expected, received);
                }
            }
        }
        // lookup for visible elements
        visible = false;
        while (!visible && iteElements.hasNext()) {
            tmp = (HtmlElement) iteElements.next();
            visible = tmp.isDisplayed();
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Check visibility(" + visible + ") for " + tmp.asXml());
            }
        }
        if (visible) {
            result.addResult(Failure.INSTANCE, context.newBlock(table, this), new PluginException("Received table has more cells than expected."));
            success = false;
        }

        return success;
    }

    /**
     * Compare terminal nodes of tables.
     * 
     * @param plugin
     *            The source plugin.
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param page
     *            The client page.
     * @param expected
     *            The expected value.
     * @param received
     *            The received value.
     * @return true, if equals, false, otherwise.
     * @throws PluginException
     *             On comparison errors.
     */
    protected boolean compareTerminal(IPlugin plugin, IContext context, IResultSet result, SgmlPage page, CellAdapter expected, HtmlElement received) throws PluginException {
        if (isTable(expected)) {
            return compareTable(context, result, page, received, expected.getNode());
        } else if (PluginCompareDate.isDate(expected)) {
            PluginCompareDate compare = UtilPlugin.create(context, PluginCompareDate.class, (Element) expected.getNode(), true);
            Object tmp = getValue(compare.getValue() != null ? compare.getValue() : expected.getValue(), compare.isEval(), context);
            String exp = String.valueOf(tmp);
            if (!expected.getValue().equals(exp)) {
                expected.append("{" + exp + "}");
            }
            String rec = received.asText();
            return PluginCompareUtils.compareDate(compare, exp, rec, context.newBlock(expected.getNode(), plugin), context, result, null);
        } else if (PluginCompareNode.isNode(expected)) {
            PluginCompareNode compare = UtilPlugin.create(context, PluginCompareNode.class, (Element) expected.getNode(), true);
            return PluginCompareUtils.compareNode(compare, (Element) expected.getNode(), received, context.newBlock(expected.getNode(), plugin), context, result);
        } else {
            PluginCompareText compare = UtilPlugin.create(context, PluginCompareText.class, (Element) expected.getNode(), true);
            Object tmp = getValue(compare.getValue() != null ? compare.getValue() : expected.getValue(), compare.isEval(), context);
            String exp = String.valueOf(tmp);
            if (!expected.getValue().equals(exp)) {
                expected.append("{" + exp + "}");
            }
            String rec = received.asText();
            return PluginCompareUtils.compare(compare.getNormalized(exp), compare.getNormalized(rec), context.newBlock(expected.getNode(), plugin), context, result, null);
        }
    }

    /**
     * Return if a given element is a table.
     * 
     * @param holder
     *            The element type.
     * @return true, if table, false otherwise.
     */
    public static boolean isTable(INodeHolder holder) {
        return holder.attributeEquals("type", "table");
    }
}