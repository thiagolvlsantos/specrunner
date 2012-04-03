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
package org.specrunner.webdriver.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.webdriver.AbstractPluginFindSingle;

/**
 * Type a text in a given element.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginType extends AbstractPluginFindSingle implements IAction {
    /**
     * Set the append mode.
     */
    private Boolean append = false;
    /**
     * Write to a given text position.
     */
    private Integer position;

    /**
     * Get the append mode.
     * 
     * @return true, if append is on, false, otherwise. Default is false.
     */
    public Boolean getAppend() {
        return append;
    }

    /**
     * To append text, without clear, set this true. Default is <b>false</b>.
     * 
     * @param append
     *            true, to append, false, otherwise.
     */
    public void setAppend(Boolean append) {
        this.append = append;
    }

    /**
     * Get the position to type text.
     * 
     * @return The text position.
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * Set the position to insert text.
     * 
     * @param position
     *            Index position to write.
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        Object tmp = getValue(getValue() != null ? getValue() : context.getNode().getValue(), true, context);
        String value = String.valueOf(tmp);
        if (getPosition() != null) {
            String str = element.getText();
            String newStr = str.substring(0, getPosition()) + value + str.substring(getPosition());
            element.clear();
            element.sendKeys(newStr);
        } else {
            if (!getAppend()) {
                element.clear();
            }
            element.sendKeys(value);
        }
        result.addResult(Status.SUCCESS, context.peek());
    }
}