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
package org.specrunner.htmlunit.actions;

import org.specrunner.context.IContext;
import org.specrunner.htmlunit.AbstractPluginFind;
import org.specrunner.htmlunit.util.WritablePage;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Partial implementation of actions in checkboxes.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginCheck extends AbstractPluginFind implements IAction {

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement[] elements) throws PluginException {
        boolean success = true;
        for (int i = 0; i < elements.length; i++) {
            if (!isCheckbox(elements[i])) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("Element " + elements[i] + " is not a checkbox."), new WritablePage(page));
                success = false;
                break;
            }
            HtmlCheckBoxInput e = (HtmlCheckBoxInput) elements[i];
            doSomething(e);
        }
        if (success) {
            result.addResult(Status.SUCCESS, context.peek());
        }
    }

    /**
     * Return if a element is a input type=checkbox.
     * 
     * @param element
     *            The element.
     * @return true, if checkbox, false otherwise.
     */
    protected boolean isCheckbox(HtmlElement element) {
        return element instanceof HtmlCheckBoxInput;
    }

    /**
     * Perform something with the checkbox.
     * 
     * @param checkbox
     *            The element.
     */
    protected abstract void doSomething(HtmlCheckBoxInput checkbox);
}
