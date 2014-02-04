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
package org.specrunner.browser.assertions;

import org.specrunner.browser.AbstractPluginFind;
import org.specrunner.browser.util.WritablePage;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;

public class PluginCheckChecked extends AbstractPluginFind implements IAssertion {

    /**
     * Check if a radio or a checkbox is checked.
     */
    private Boolean checked;

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement[] elements) throws PluginException {
        if (checked == null) {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Set plugin parameter 'checked' to true or false."));
            return;
        }
        boolean error = false;
        for (HtmlElement element : elements) {
            if (!(element instanceof HtmlCheckBoxInput) && !(element instanceof HtmlRadioButtonInput)) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("Element " + getFinder().resume(context) + " is not a checkbox or radio is " + element.getClass().getName()), new WritablePage(page));
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
                if (checked != onComponent) {
                    result.addResult(Status.FAILURE, context.peek(), new PluginException("Element " + getFinder().resume(context) + " should be '" + (checked ? "checked" : "unchecked") + "' but is '" + (onComponent ? "checked" : "unchecked") + "'."), new WritablePage(page));
                    error = true;
                }
            }
        }
        if (!error) {
            result.addResult(Status.SUCCESS, context.peek());
        }
    }

}