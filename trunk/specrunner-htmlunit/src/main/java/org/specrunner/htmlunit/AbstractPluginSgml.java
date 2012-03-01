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
package org.specrunner.htmlunit;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebWindow;

/**
 * Partial implementation of a plugin which is browser aware and the content of
 * current browser pase is a SgmlPage.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginSgml extends AbstractPluginBrowserAware {

    @Override
    protected void doEnd(IContext context, IResultSet result, WebClient client) throws PluginException {
        WebWindow window = client.getCurrentWindow();
        Page page = window.getEnclosedPage();
        if (page instanceof SgmlPage) {
            doEnd(context, result, client, (SgmlPage) page);
        } else {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Enclosed page (" + page + ") is not an SgmlPage."));
        }
    }

    /**
     * Method delegation which receives the SgmlPage to be used by subclasses.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result set.
     * @param client
     *            The client.
     * @param page
     *            The SgmlPage page.
     * @throws PluginException
     *             On execution errors.
     */
    protected abstract void doEnd(IContext context, IResultSet result, WebClient client, SgmlPage page) throws PluginException;
}