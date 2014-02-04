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
package org.specrunner.webdriver.assertions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.webdriver.AbstractPluginFind;
import org.specrunner.webdriver.util.WritablePage;

/**
 * Check if elements are selected.
 * 
 * @author Thiago Santos
 * 
 */
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
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement[] elements) throws PluginException {
        if (getChecked() == null) {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Set plugin parameter 'checked' to true or false."));
            return;
        }
        boolean error = false;
        for (WebElement element : elements) {
            if (isCheckbox(element) || isRadio(element)) {
                boolean onComponent = element.isSelected();
                if (getChecked() != onComponent) {
                    result.addResult(Status.FAILURE, context.peek(), new PluginException("Element " + getFinder().resume(context) + " should be '" + (checked ? "checked" : "unchecked") + "' but is '" + (onComponent ? "checked" : "unchecked") + "'."), new WritablePage(client));
                    error = true;
                }
            } else {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("Element " + getFinder().resume(context) + " is not a checkbox or radio is " + element.getTagName()), new WritablePage(client));
                error = true;
            }
        }
        if (!error) {
            result.addResult(Status.SUCCESS, context.peek());
        }
    }

    protected boolean isRadio(WebElement element) {
        return "input".equals(element.getTagName()) && "radio".equals(element.getAttribute("type"));
    }

    protected boolean isCheckbox(WebElement element) {
        return "input".equals(element.getTagName()) && "checkbox".equals(element.getAttribute("type"));
    }

}