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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.plugins.IAssertion;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.webdriver.AbstractPluginFindSingle;
import org.specrunner.webdriver.util.WritablePage;

/**
 * Check if body or a given element contains a text.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginContains extends AbstractPluginFindSingle implements IAssertion {

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        getFinder().setParameter("by", "xpath://html");
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        Object obj = getValue(getValue() != null ? getValue() : context.getNode().getValue(), true, context);
        String value = getNormalized(String.valueOf(obj));
        String content = getNormalized(element.getText());
        if (test(content, value)) {
            result.addResult(Status.SUCCESS, context.peek());
        } else {
            result.addResult(Status.FAILURE, context.peek(), new PluginException(getMessage(context, value)), new WritablePage(client));
        }
    }

    /**
     * Check if content contains the value.
     * 
     * @param content
     *            The content.
     * @param value
     *            The value.
     * @return true, if contains, false, otherwise.
     */
    protected boolean test(String content, String value) {
        return content.contains(value);
    }

    /**
     * The error message in case o test failure.
     * 
     * @param context
     *            The context.
     * @param value
     *            The value.
     * @return The message.
     * @throws PluginException
     *             On message construction errors.
     */
    protected String getMessage(IContext context, String value) throws PluginException {
        return "Element " + getFinder().resume(context) + " does not contain '" + value + "'.";
    }
}
