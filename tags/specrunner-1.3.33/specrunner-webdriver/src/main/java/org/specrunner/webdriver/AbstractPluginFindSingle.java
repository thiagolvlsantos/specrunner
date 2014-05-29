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
package org.specrunner.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;

/**
 * A specialization of AbstractPluginFind to be overridden by actions that take
 * only one element to perform its action. The default selection is the first
 * element found. If multiple elements are found by the search strategy use
 * 'index' attribute to specify which one to be selected.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginFindSingle extends AbstractPluginFind {

    /**
     * The element index.
     */
    protected int index = 0;

    /**
     * Element index, to be used when XPath returns more than one element.
     * 
     * @return The element index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the index position.
     * 
     * @param index
     *            The index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement[] elements) throws PluginException {
        if (index < 0 || index > elements.length) {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Index out of range '" + index + "', max = '" + elements.length + "'."));
            return;
        }
        process(context, result, client, elements[index]);
    }

    /**
     * Method delegation which receives the selected element to be used by
     * subclasses.
     * 
     * @param context
     *            The test context.
     * @param result
     *            A result set.
     * @param client
     *            The browser.
     * @param element
     *            The selected element.
     * @throws PluginException
     *             On execution errors.
     */
    protected abstract void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException;
}