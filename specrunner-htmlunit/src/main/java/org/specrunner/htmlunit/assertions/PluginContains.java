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

import org.specrunner.context.IContext;
import org.specrunner.htmlunit.AbstractPluginFindSingle;
import org.specrunner.htmlunit.util.WritablePage;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Check if body or a given element contains a text.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginContains extends AbstractPluginFindSingle {
    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        try {
            getFinderInstance().getParameters().setParameter("by", "xpath://html", context);
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement element) throws PluginException {
        Object obj = getValue(getValue() != null ? getValue() : context.getNode().getValue(), true, context);
        String value = getNormalized(String.valueOf(obj));
        String content = getNormalized(element.asText());
        if (test(content, value)) {
            result.addResult(Success.INSTANCE, context.peek());
        } else {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException(getMessage(context, value)), new WritablePage(page));
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
        return getFinderInstance().resume(context) + " does not contain '" + value + "'.";
    }
}
