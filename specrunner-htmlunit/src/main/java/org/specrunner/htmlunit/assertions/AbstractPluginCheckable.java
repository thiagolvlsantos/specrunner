/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.htmlunit.AbstractPluginFind;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;

/**
 * Partial implementation of actions in checkboxes.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginCheckable extends AbstractPluginFind {
    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement[] elements) throws PluginException {
        boolean error = false;
        for (HtmlElement element : elements) {
            if (!(element instanceof HtmlCheckBoxInput) && !(element instanceof HtmlRadioButtonInput)) {
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Element " + getFinderInstance().resume(context) + " is not a checkbox or radio is " + element.getClass().getName()), SRServices.get(IWritableFactoryManager.class).get(Page.class).newWritable(page));
                error = true;
            } else {
                boolean onComponent = false;
                if (element instanceof HtmlCheckBoxInput) {
                    HtmlCheckBoxInput in = (HtmlCheckBoxInput) element;
                    onComponent = in.isChecked();
                }
                if (element instanceof HtmlRadioButtonInput) {
                    HtmlRadioButtonInput in = (HtmlRadioButtonInput) element;
                    onComponent = in.isChecked();
                }
                if (expected() != onComponent) {
                    result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Element " + getFinderInstance().resume(context) + " should be '" + (expected() ? "checked" : "unchecked") + "' but is '" + (onComponent ? "checked" : "unchecked") + "'."), SRServices.get(IWritableFactoryManager.class).get(Page.class).newWritable(page));
                    error = true;
                }
            }
        }
        if (!error) {
            result.addResult(Success.INSTANCE, context.peek());
        }
    }

    /**
     * The expected value.
     * 
     * @return true, of expected is marked, false, otherwise.
     */
    protected abstract boolean expected();
}
