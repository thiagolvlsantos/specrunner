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
package org.specrunner.webdriver.assertions;

import java.util.Iterator;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilNode;
import org.specrunner.util.UtilXPath;
import org.specrunner.util.impl.CellAdapter;
import org.specrunner.util.impl.RowAdapter;
import org.specrunner.util.impl.TableAdapter;
import org.specrunner.webdriver.AbstractPluginFindSingle;
import org.specrunner.webdriver.util.WritablePage;

/**
 * Compare tables.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCompareTable extends AbstractPluginFindSingle implements IAssertion {

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        Node node = context.getNode();
        boolean success = compareTable(context, result, client, element, node);
        if (!success) {
            result.addResult(Status.FAILURE, context.newBlock(node, this), new PluginException("Tables do not match."), new WritablePage(client));
        }
    }

    protected boolean compareTable(IContext context, IResultSet result, WebDriver client, WebElement element, Node node) throws PluginException {
        boolean success = true;

        Nodes tables = node.query("descendant-or-self::table");
        Node table = UtilXPath.getHighest(tables);
        if (table == null) {
            result.addResult(Status.FAILURE, context.newBlock(node, this), new PluginException("Table to be compared is not specified."));
            return false;
        }
        TableAdapter tableExpected = UtilNode.newTableAdapter((Element) table);

        List<WebElement> tablesReceived = element.findElements(By.xpath("descendant-or-self::table"));
        if (tablesReceived.isEmpty()) {
            result.addResult(Status.FAILURE, context.newBlock(table, this), new PluginException("Expected table not present in input."), new WritablePage(client));
            return false;
        }
        WebElement tableReceived = tablesReceived.get(0);

        List<WebElement> tableCaptions = tableReceived.findElements(By.xpath("child::caption"));
        Iterator<WebElement> iteCaptions = tableCaptions.iterator();
        for (CellAdapter expected : tableExpected.getCaptions()) {
            if (!iteCaptions.hasNext()) {
                result.addResult(Status.FAILURE, context.newBlock(expected.getElement(), this), new PluginException("Caption cells missing in received table."));
                success = false;
                continue;
            }
            WebElement received = iteCaptions.next();
            while (!received.isDisplayed() && iteCaptions.hasNext()) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Ignore table invisible caption:" + received);
                }
                received = iteCaptions.next();
            }
            if (received.isDisplayed()) {
                success = success & compareTerminal(this, context, result, client, expected, received);
            }
        }
        // lookup for visible elements
        boolean visible = false;
        WebElement tmp;
        while (!visible && iteCaptions.hasNext()) {
            tmp = iteCaptions.next();
            visible = tmp.isDisplayed();
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Check visibility(" + visible + ") for " + tmp);
            }
        }
        if (visible) {
            result.addResult(Status.FAILURE, context.newBlock(table, this), new PluginException("Received table has more captions than expected."));
            success = false;
        }

        List<WebElement> tableElements = tableReceived.findElements(By.xpath("child::tr/th | child::tr/td | child::thead/tr/th | child::thead/tr/td | child::tbody/tr/th | child::tbody/tr/td"));
        Iterator<WebElement> iteElements = tableElements.iterator();
        for (RowAdapter rowsExpected : tableExpected.getRows()) {
            for (CellAdapter expected : rowsExpected.getCells()) {
                if (!iteElements.hasNext()) {
                    result.addResult(Status.FAILURE, context.newBlock(expected.getElement(), this), new PluginException("Cell missing in received table."));
                    success = false;
                    continue;
                }
                WebElement received = iteElements.next();
                while (!received.isDisplayed() && iteElements.hasNext()) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Ignore table invisible element:" + received);
                    }
                    received = iteElements.next();
                }
                if (received.isDisplayed()) {
                    success = success & compareTerminal(this, context, result, client, expected, received);
                }
            }
        }
        // lookup for visible elements
        visible = false;
        while (!visible && iteElements.hasNext()) {
            tmp = iteElements.next();
            visible = tmp.isDisplayed();
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Check visibility(" + visible + ") for " + tmp);
            }
        }
        if (visible) {
            result.addResult(Status.FAILURE, context.newBlock(table, this), new PluginException("Received table has more cells than expected."));
            success = false;
        }

        return success;
    }

    protected boolean compareTerminal(IPlugin plugin, IContext context, IResultSet result, WebDriver client, CellAdapter expected, WebElement received) throws PluginException {
        if (isTable(expected.getElement())) {
            return compareTable(context, result, client, received, expected.getElement());
        } else if (PluginCompareDate.isDate(expected.getElement())) {
            PluginCompareDate compare = UtilPlugin.create(context, PluginCompareDate.class, expected.getElement(), true);
            Object tmp = getValue(compare.getValue() != null ? compare.getValue() : expected.getValue(), true, context);
            String exp = String.valueOf(tmp);
            String rec = received.getText();
            return PluginCompareUtils.compareDate(compare.getFormat(), compare.getTolerance(), exp, rec, context.newBlock(expected.getElement(), plugin), context, result, client);
        } else {
            PluginCompare compare = UtilPlugin.create(context, PluginCompareDate.class, expected.getElement(), true);
            Object tmp = getValue(compare.getValue() != null ? compare.getValue() : expected.getValue(), true, context);
            String exp = String.valueOf(tmp).trim();
            String rec = received.getText().trim();
            return PluginCompareUtils.compare(compare.getNormalized(exp), compare.getNormalized(rec), context.newBlock(expected.getElement(), plugin), context, result, client);
        }
    }

    public static boolean isTable(Element element) {
        CellAdapter ca = new CellAdapter(element);
        return ca.hasAttribute("type") && ca.getAttribute("type").equalsIgnoreCase("table");
    }
}