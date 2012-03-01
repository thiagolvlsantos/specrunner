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

import java.util.LinkedList;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.webdriver.AbstractPluginFindSingle;
import org.specrunner.webdriver.util.WritablePage;

/**
 * Check elements selected or not of a given 'select'.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCheckSelection extends AbstractPluginFindSingle implements IAssertion {

    private Boolean content;
    private Boolean selection;

    /**
     * Check the content of a select.
     * 
     * @return true, to check content.
     */
    public Boolean getContent() {
        return content;
    }

    public void setContent(Boolean content) {
        this.content = content;
    }

    /**
     * Check the selection of a select.
     * 
     * @return true, to check selected items.
     */
    public Boolean getSelection() {
        return selection;
    }

    public void setSelection(Boolean selection) {
        this.selection = selection;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        if (getContent() == null && getSelection() == null) {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Set plugin content or selection."));
            return;
        }
        int error = checkContent(context, result, client, element);
        error = error + checkSelection(context, result, client, element);
        if (error == 0) {
            result.addResult(Status.SUCCESS, context.peek());
        }
    }

    protected int checkContent(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        int error = 0;
        if (getContent() != null) {
            if (!isSelect(element)) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("Element on " + getFinder().resume(context) + " is not a select is " + element.getClass().getName()), new WritablePage(client));
                error = 1;
            } else {
                Node node = context.getNode();
                Nodes nodes = node.query("descendant::li");
                List<WebElement> options = element.findElements(By.xpath("descendant::option"));
                error = testList(context, result, client, nodes, options, content);
            }
        }
        return error;
    }

    protected boolean isSelect(WebElement element) {
        return element.getTagName().equalsIgnoreCase("select");
    }

    protected int checkSelection(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        int error = 0;
        if (getSelection() != null) {
            if (!isSelect(element)) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("Element on " + getFinder().resume(context) + " is not a select is " + element.getClass().getName()), new WritablePage(client));
                error = 1;
            } else {
                Node node = context.getNode();
                Nodes nodes = node.query("descendant::li");
                if (nodes.size() == 0) {
                    nodes = new Nodes(node);
                }
                List<WebElement> tmp = element.findElements(By.xpath("descendant::option"));
                List<WebElement> options = new LinkedList<WebElement>();
                for (WebElement we : tmp) {
                    if (we.isSelected()) {
                        options.add(we);
                    }
                }
                error = testList(context, result, client, nodes, options, content);
            }
        }
        return error;
    }

    @SuppressWarnings("serial")
    protected int testList(IContext context, IResultSet result, WebDriver client, Nodes nodes, List<WebElement> options, Boolean content) {
        int error = 0;
        String[] expecteds = new String[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            expecteds[i] = nodes.get(i).getValue();
        }
        String[] receiveds = new String[options.size()];
        for (int i = 0; i < receiveds.length; i++) {
            receiveds[i] = options.get(i).getText();
        }
        if (expecteds.length != receiveds.length) {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Number of itens (" + receiveds.length + ") in " + (content != null && content ? "content" : "selected") + " is different from expected ones (" + expecteds.length + ")."), new WritablePage(client));
            error = 1;
        } else {
            for (int i = 0; i < expecteds.length; i++) {
                if (!expecteds[i].equals(receiveds[i])) {
                    IStringAligner al = SpecRunnerServices.get(IStringAlignerFactory.class).align(expecteds[i], receiveds[i]);
                    result.addResult(Status.FAILURE, context.newBlock(nodes.get(i), this), new DefaultAlignmentException(al) {
                        @Override
                        public String getMessage() {
                            return "Selection options are diferent (" + super.getMessage() + ").";
                        }
                    });
                    error++;
                }
            }
            if (error > 0) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("The element(s) expected and received do(es) not match."), new WritablePage(client));
            }
        }
        return error;
    }
}
