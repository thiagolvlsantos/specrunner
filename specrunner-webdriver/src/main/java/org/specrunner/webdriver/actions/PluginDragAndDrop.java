/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.webdriver.AbstractPluginFindSingle;
import org.specrunner.webdriver.IFinder;

/**
 * Drag the element in 'by' attribute to the destination in 'target', or use
 * xoffset and yoffset.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginDragAndDrop extends AbstractPluginFindSingle {

    /**
     * The target element.
     */
    private String target;
    /**
     * The x offset gap.
     */
    private Integer xoffset;
    /**
     * The y offset gap.
     */
    private Integer yoffset;

    /**
     * Gets the target of paste action. i.e.
     * <code>target="xpath://span[@id='destination']"</code>. The same search
     * options provided by '<code>by</code>' attribute.
     * 
     * @return The target object.
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the target object.
     * 
     * @param target
     *            The target.
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Gets the x offset value.
     * 
     * @return The x offset.
     */
    public Integer getXoffset() {
        return xoffset;
    }

    /**
     * Sets the x offset value.
     * 
     * @param xoffset
     *            The offset.
     */
    public void setXoffset(Integer xoffset) {
        this.xoffset = xoffset;
    }

    /**
     * Gets the y offset method.
     * 
     * @return The y offset.
     */
    public Integer getYoffset() {
        return yoffset;
    }

    /**
     * Sets the y offset.
     * 
     * @param yoffset
     *            The y offset.
     */
    public void setYoffset(Integer yoffset) {
        this.yoffset = yoffset;
    }

    @Override
    public ActionType getActionType() {
        return org.specrunner.plugins.type.Command.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        if (getTarget() == null && getXoffset() == null && getYoffset() == null) {
            throw new PluginException("To use drag and drop you should specify 'target' attribute using the same syntax of attribute 'by', or specify 'xoffset' and 'yoffset' attributes.");
        }
        Action ac = null;
        if (getTarget() != null) {
            IFinder finder = getFinderInstance();
            try {
                finder.getParameters().setParameter("by", getTarget(), context);
            } catch (Exception e) {
                throw new PluginException(e);
            }

            List<WebElement> list = finder.find(context, result, client);
            if (list.isEmpty()) {
                throw new PluginException("Element " + finder.resume(context) + " not found.");
            }
            WebElement destination = list.get(0);
            ac = new Actions(client).dragAndDrop(element, destination).build();
        } else if (getXoffset() == null && getYoffset() == null) {
            ac = new Actions(client).dragAndDropBy(element, getXoffset(), getYoffset()).build();
        } else {
            throw new PluginException("You should specify both 'xoffset' and 'yoffset' attributes.");
        }
        ac.perform();
        result.addResult(Success.INSTANCE, context.peek());
    }
}
