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

import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.htmlunit.AbstractPluginFind;
import org.specrunner.htmlunit.util.WritablePage;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Check if an id, name, value, xpath, etc is present.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginNotPresent extends AbstractPluginFind {

    @Override
    protected void doEnd(IContext context, IResultSet result, WebClient client, SgmlPage page) throws PluginException {
        List<?> list = getFinderInstance(context).find(context, result, client, page);
        if (list == null || list.isEmpty()) {
            result.addResult(Status.SUCCESS, context.peek());
        } else {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Element found for " + getFinder().resume(context) + "."), new WritablePage(page));
        }
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement[] elements) throws PluginException {
        // useless.
    }
}