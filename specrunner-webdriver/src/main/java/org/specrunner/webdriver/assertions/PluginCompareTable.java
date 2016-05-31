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
package org.specrunner.webdriver.assertions;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SRServices;
import org.specrunner.comparators.ComparatorException;
import org.specrunner.comparators.IComparator;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.UtilPlugin;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.result.status.Failure;
import org.specrunner.util.UtilLog;
import org.specrunner.util.math.Range;
import org.specrunner.util.xom.UtilNode;
import org.specrunner.util.xom.node.CellAdapter;
import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.RowAdapter;
import org.specrunner.util.xom.node.TableAdapter;
import org.specrunner.util.xom.node.UtilTable;
import org.specrunner.webdriver.AbstractPluginFindSingle;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

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
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        Node node = context.getNode();
        boolean success = compareTable(context, result, client, element, node);
        if (!success) {
            result.addResult(Failure.INSTANCE, context.newBlock(node, this), new PluginException("Tables do not match."), SRServices.get(IWritableFactoryManager.class).get(WebDriver.class).newWritable(client));
        }
    }

    /**
     * Compare a table.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * @param element
     *            The element.
     * @param node
     *            The node.
     * @return The result of comparison.
     * @throws PluginException
     *             On evaluation error.
     */
    protected boolean compareTable(IContext context, IResultSet result, WebDriver client, WebElement element, Node node) throws PluginException {
        boolean success = true;

        Nodes tables = getTables(node);
        Node table = UtilNode.getHighest(tables);
        if (table == null) {
            result.addResult(Failure.INSTANCE, context.newBlock(node, this), new PluginException("Table to be compared is not specified."));
            return false;
        }
        TableAdapter tableExpected = UtilTable.newTable(table);

        List<WebElement> tablesReceived = getTables(element);
        if (tablesReceived.isEmpty()) {
            result.addResult(Failure.INSTANCE, context.newBlock(table, this), new PluginException("Expected table not present in input."));
            return false;
        }
        WebElement tableReceived = tablesReceived.get(0);

        List<WebElement> tableCaptions = getCaptions(tableExpected, tableReceived);
        Iterator<WebElement> iteCaptions = tableCaptions.iterator();
        for (CellAdapter expected : tableExpected.getCaptions()) {
            if (!iteCaptions.hasNext()) {
                result.addResult(Failure.INSTANCE, context.newBlock(expected.getNode(), this), new PluginException("Caption cells missing in received table."));
                success = false;
                continue;
            }
            WebElement received = iteCaptions.next();
            if (prepare != null) {
                prepare.prepare(this, client, received);
            }
            while (!received.isDisplayed() && iteCaptions.hasNext()) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Ignore table invisible caption:" + received);
                }
                received = iteCaptions.next();
                if (prepare != null) {
                    prepare.prepare(this, client, received);
                }
            }
            if (!expected.hasAttribute(UtilNode.IGNORE) && received.isDisplayed()) {
                success = success & compareTerminal(this, context, result, client, expected, received);
            }
        }
        // lookup for visible elements
        boolean visible = false;
        WebElement tmp;
        while (!visible && iteCaptions.hasNext()) {
            tmp = iteCaptions.next();
            if (prepare != null) {
                prepare.prepare(this, client, tmp);
            }
            visible = tmp.isDisplayed();
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Check visibility(" + visible + ") for " + tmp);
            }
        }
        if (visible) {
            result.addResult(Failure.INSTANCE, context.newBlock(table, this), new PluginException("Received table has more captions than expected."));
            success = false;
        }

        List<WebElement> tableElements = getCells(tableExpected, tableReceived);
        Iterator<WebElement> iteElements = tableElements.iterator();
        for (RowAdapter rowsExpected : tableExpected.getRows()) {
            for (CellAdapter expected : rowsExpected.getCells()) {
                if (!iteElements.hasNext()) {
                    result.addResult(Failure.INSTANCE, context.newBlock(expected.getNode(), this), new PluginException("Cell missing in received table."));
                    success = false;
                    continue;
                }
                WebElement received = iteElements.next();
                if (prepare != null) {
                    prepare.prepare(this, client, received);
                }
                while (!received.isDisplayed() && iteElements.hasNext()) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Ignore table invisible element:" + received);
                    }
                    received = iteElements.next();
                    if (prepare != null) {
                        prepare.prepare(this, client, received);
                    }
                }
                if (!expected.hasAttribute(UtilNode.IGNORE) && received.isDisplayed()) {
                    success = success & compareTerminal(this, context, result, client, expected, received);
                }
            }
        }
        // lookup for visible elements
        visible = false;
        while (!visible && iteElements.hasNext()) {
            tmp = iteElements.next();
            if (prepare != null) {
                prepare.prepare(this, client, tmp);
            }
            visible = tmp.isDisplayed();
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Check visibility(" + visible + ") for " + tmp);
            }
        }
        if (visible) {
            result.addResult(Failure.INSTANCE, context.newBlock(table, this), new PluginException("Received table has more cells than expected."));
            success = false;
        }

        return success;
    }

    /**
     * Get tables.
     * 
     * @param expected
     *            Expected table.
     * @return List of table elements.
     */
    protected Nodes getTables(Node expected) {
        return expected.query("descendant-or-self::table");
    }

    /**
     * Get tables.
     * 
     * @param received
     *            Received element.
     * @return List of table elements.
     */
    protected List<WebElement> getTables(WebElement received) {
        return received.findElements(By.xpath("descendant-or-self::table"));
    }

    /**
     * Get received captions.
     * 
     * @param tableExpected
     *            Expected table.
     * @param tableReceived
     *            Received table.
     * @return List of elements to compare.
     */
    protected List<WebElement> getCaptions(TableAdapter tableExpected, WebElement tableReceived) {
        return tableReceived.findElements(By.xpath("child::caption"));
    }

    /**
     * Get received cells.
     * 
     * @param tableExpected
     *            Expected table.
     * @param tableReceived
     *            Received table.
     * @return List of elements to compare.
     */
    protected List<WebElement> getCells(TableAdapter tableExpected, WebElement tableReceived) {
        List<WebElement> result = new LinkedList<WebElement>();
        List<WebElement> rows = tableReceived.findElements(By.xpath("child::tr | child::thead/tr | child::tbody/tr"));
        List<Range> rowRanges = Range.getRanges(tableExpected.getAttribute("rows"), tableExpected.getAttribute("separator"), 0, rows.size());
        List<Range> colRanges = Range.getRanges(tableExpected.getAttribute("cols"), tableExpected.getAttribute("separator"), 0, Integer.MAX_VALUE);
        int sourceRows = 0;
        int tmpRowRange = 0;
        for (int i = 0; i < rows.size() && tmpRowRange < rowRanges.size(); i++) {
            Range rowRange = rowRanges.get(tmpRowRange);
            boolean skipRange = false;
            while (rowRange.between(i) && i < rows.size()) {
                skipRange = true;
                List<WebElement> cols = rows.get(i).findElements(By.xpath("child::th | child::td"));
                int sourceCells = 0;
                int tmpColRange = 0;
                for (int j = 0; j < cols.size() && tmpColRange < colRanges.size(); j++) {
                    Range colRange = colRanges.get(tmpColRange);
                    while (colRange.between(j) && j < cols.size()) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            CellAdapter cell = tableExpected.getRows().get(sourceRows).getCell(sourceCells);
                            cell.setAttribute("title", cell.hasAttribute("title") ? cell.getAttribute("title") + " (" + i + "," + j + ")" : " (" + i + "," + j + ")");
                        }
                        result.add(cols.get(j));
                        sourceCells++;
                        j++;
                    }
                    tmpColRange++;
                }
                sourceRows++;
                i++;
            }
            if (skipRange) {
                tmpRowRange++;
            }
        }
        return result;
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
     * @param client
     *            The web driver.
     * @param expected
     *            The expected value.
     * @param received
     *            The received value.
     * @return true, if equals, false, otherwise.
     * @throws PluginException
     *             On comparison errors.
     */
    protected boolean compareTerminal(IPlugin plugin, IContext context, IResultSet result, WebDriver client, CellAdapter expected, WebElement received) throws PluginException {
        if (isTable(expected)) {
            return compareTable(context, result, client, received, expected.getNode());
        } else if (PluginCompareDate.isDate(expected)) {
            PluginCompareDate compare = UtilPlugin.create(context, PluginCompareDate.class, (Element) expected.getNode(), true);
            Object tmp = getValue(compare.getValue() != null ? compare.getValue() : expected.getValue(context), compare.isEval(), context);
            String exp = String.valueOf(tmp);
            if (!expected.getValue(context).equals(exp)) {
                expected.append("{" + exp + "}");
            }
            String rec = received.getText();
            IComparator comparator;
            try {
                comparator = expected.getComparator(SRServices.getComparatorManager().get("time"));
            } catch (ComparatorException e) {
                result.addResult(Failure.INSTANCE, context.newBlock(expected.getNode(), this), e);
                return false;
            }
            return PluginCompareUtils.compareDate(compare, exp, rec, comparator, context.newBlock(expected.getNode(), plugin), context, result, null);
        } else if (PluginCompareNode.isNode(expected)) {
            PluginCompareNode compare = UtilPlugin.create(context, PluginCompareNode.class, (Element) expected.getNode(), true);
            return PluginCompareUtils.compareNode(compare, (Element) expected.getNode(), received, context.newBlock(expected.getNode(), plugin), context, result);
        } else {
            PluginCompareText compare = UtilPlugin.create(context, PluginCompareText.class, (Element) expected.getNode(), true);
            Object tmp = getValue(compare.getValue() != null ? compare.getValue() : expected.getValue(context), compare.isEval(), context);
            String exp = String.valueOf(tmp);
            if (!expected.getValue(context).equals(exp)) {
                expected.append("{" + exp + "}");
            }
            String rec = received.getText();
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
