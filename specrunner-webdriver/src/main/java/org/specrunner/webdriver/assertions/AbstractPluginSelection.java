/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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

import java.util.List;

import nu.xom.Nodes;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.aligner.core.DefaultAlignmentException;
import org.specrunner.webdriver.AbstractPluginFindSingle;

/**
 * Check elements of a selection.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginSelection extends AbstractPluginFindSingle {
    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        if (isSelect(element)) {
            if (checkSelection(context, result, client, element) == 0) {
                result.addResult(Success.INSTANCE, context.peek());
            }
        } else {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Element on " + getFinderInstance().resume(context) + " is not a select is " + element.getClass().getName()), SRServices.get(IWritableFactoryManager.class).get(WebDriver.class).newWritable(client));
        }
    }

    /**
     * Check if element is a select.
     * 
     * @param element
     *            The select form.
     * @return true, if is select, false, otherwise.
     */
    protected boolean isSelect(WebElement element) {
        return element.getTagName().equalsIgnoreCase("select");
    }

    /**
     * Perform the selection test.
     * 
     * @param context
     *            The test.
     * @param result
     *            The result.
     * @param client
     *            The web driver.
     * @param element
     *            The element.
     * @return Number of errors.
     * @throws PluginException
     *             On comparison errors.
     */
    protected abstract int checkSelection(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException;

    /**
     * Perform tests on a list.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result set.
     * @param client
     *            The client.
     * @param nodes
     *            The nodes to be compared.
     * @param options
     *            The select options.
     * @param content
     *            true, to check options, false, to check selected.
     * @return Number of inconsistencies.
     */
    @SuppressWarnings("serial")
    protected int testList(IContext context, IResultSet result, WebDriver client, Nodes nodes, List<WebElement> options, Boolean content) {
        int error = 0;
        String[] expecteds = new String[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            expecteds[i] = getNormalized(nodes.get(i).getValue());
        }
        String[] receiveds = new String[options.size()];
        for (int i = 0; i < receiveds.length; i++) {
            receiveds[i] = getNormalized(options.get(i).getText());
        }
        if (expecteds.length != receiveds.length) {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Number of items (" + receiveds.length + ") in " + (content != null && content ? "select" : "selected") + " is different from expected ones (" + expecteds.length + ")."), SRServices.get(IWritableFactoryManager.class).get(WebDriver.class).newWritable(client));
            error = 1;
        } else {
            for (int i = 0; i < expecteds.length; i++) {
                if (!expecteds[i].equals(receiveds[i])) {
                    result.addResult(Failure.INSTANCE, context.newBlock(nodes.get(i), this), new DefaultAlignmentException(expecteds[i], receiveds[i]) {
                        @Override
                        public String getMessage() {
                            return "Selection options are diferent (" + super.getMessage() + ").";
                        }
                    });
                    error++;
                }
            }
            if (error > 0) {
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("The element(s) expected and received do(es) not match."), SRServices.get(IWritableFactoryManager.class).get(WebDriver.class).newWritable(client));
            }
        }
        return error;
    }
}
