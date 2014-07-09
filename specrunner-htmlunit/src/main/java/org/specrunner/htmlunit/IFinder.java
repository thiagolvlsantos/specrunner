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
package org.specrunner.htmlunit;

import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.parameters.IParameterHolder;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Something that finds HtmlElement in pages.
 * 
 * @author Thiago Santos
 * 
 */
public interface IFinder extends IParameterHolder {

    /**
     * Clear any previous finder settings.
     */
    void reset();

    /**
     * Return if finder is initialized.
     * 
     * @return true, if initialized, false, otherwise. Every reset sets
     *         initialized to false.
     */
    boolean isInitialized();

    /**
     * Set initialized status.
     * 
     * @param initialized
     *            true, to initialized, false, otherwise.
     */
    void setInitialized(boolean initialized);

    /**
     * Get the XPath expression corresponding to expected element(s).
     * 
     * @param context
     *            The test context.
     * 
     * @return The XPath if it exists, null, otherwise.
     * @throws PluginException
     *             On XPath generation errors.
     */
    String getXPath(IContext context) throws PluginException;

    /**
     * Returns a list of elements.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The result set.
     * @param client
     *            The browser.
     * @param page
     *            The page.
     * @return A list empty or not of elements.
     * @throws PluginException
     *             On filter errors.
     */
    List<?> find(IContext context, IResultSet result, WebClient client, SgmlPage page) throws PluginException;

    /**
     * The representation of the filter.
     * 
     * @param context
     *            The test context.
     * @return A string that 'explain' the search used.
     * @throws PluginException
     *             On detail errors.
     */
    String resume(IContext context) throws PluginException;
}