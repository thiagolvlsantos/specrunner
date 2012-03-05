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
package org.specrunner.htmlunit.assertions;

import org.specrunner.context.IContext;
import org.specrunner.htmlunit.AbstractPluginFindSingle;
import org.specrunner.htmlunit.util.WritablePage;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class PluginContains extends AbstractPluginFindSingle implements IAssertion {

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        getFinder().setParameter("by", "xpath://html");
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement element) throws PluginException {
        Object obj = getValue(getValue() != null ? getValue() : context.getNode().getValue(), true, context);
        String value = getNormalized(String.valueOf(obj));
        String content = getNormalized(element.asText());
        if (test(content, value)) {
            result.addResult(Status.SUCCESS, context.peek());
        } else {
            result.addResult(Status.FAILURE, context.peek(), new PluginException(getMessage(context, value)), new WritablePage(page));
        }
    }

    protected boolean test(String content, String value) {
        return content.contains(value);
    }

    protected String getMessage(IContext context, String value) throws PluginException {
        return getFinder().resume(context) + " does not contain '" + value + "'.";
    }
}
