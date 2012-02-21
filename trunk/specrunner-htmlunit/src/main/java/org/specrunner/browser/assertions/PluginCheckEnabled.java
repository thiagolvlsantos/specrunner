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
import com.gargoylesoftware.htmlunit.html.DisabledElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class PluginCheckEnabled extends AbstractPluginFind implements IAssertion {

    /**
     * Check if a component is enabled.
     */
    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement[] elements) throws PluginException {
        if (enabled == null) {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Set plugin attribute 'enabled' to true or false."));
            return;
        }
        boolean error = false;
        for (HtmlElement element : elements) {
            if (!(element instanceof DisabledElement)) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("Element on " + getFinder().resume(context) + " is not an " + DisabledElement.class.getName() + " is " + element.getClass().getName()), new WritablePage(page));
                error = true;
            } else {
                DisabledElement in = (DisabledElement) element;
                if (!(enabled == !in.isDisabled())) {
                    result.addResult(Status.FAILURE, context.peek(), new PluginException("Element '" + getFinder().resume(context) + " should be '" + (enabled ? "enabled" : "disabled") + "' but is '" + (!in.isDisabled() ? "enabled" : "disabled") + "'."), new WritablePage(page));
                    error = true;
                }
            }
        }
        if (!error) {
            result.addResult(Status.SUCCESS, context.peek());
        }
    }
}