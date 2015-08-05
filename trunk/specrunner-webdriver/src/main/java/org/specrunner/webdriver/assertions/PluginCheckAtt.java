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
package org.specrunner.webdriver.assertions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.INodeHolderFactory;
import org.specrunner.webdriver.AbstractPluginFindSingle;

/**
 * Check if a given attribute element contains a text.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCheckAtt extends AbstractPluginFindSingle {
    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    /**
     * The attribute to be checked against value.
     */
    private String attribute;

    /**
     * Gets the attribute.
     * 
     * @return The attribute.
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * The attribute value.
     * 
     * @param attribute
     *            The value.
     */
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        String attRaw = element.getAttribute(String.valueOf(attribute));
        Object attName = getValue(attRaw, true, context);
        String attValue = element.getAttribute(String.valueOf(attName));
        INodeHolder nh = SRServices.get(INodeHolderFactory.class).newHolder(context.getNode());
        Object value = nh.getObject(context, true);
        if (test(attValue, value)) {
            result.addResult(Success.INSTANCE, context.peek());
        } else {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException(getMessage(context, value)), SRServices.get(IWritableFactoryManager.class).get(WebDriver.class).newWritable(client));
        }
    }

    /**
     * The testing condition.
     * 
     * @param attValue
     *            The attribute returned value.
     * @param value
     *            The value that must be part of attribute value.
     * @return true, is attValue contains value.
     */
    protected boolean test(Object attValue, Object value) {
        return (value == null && attValue == null) || getNormalized(String.valueOf(attValue)).contains(getNormalized(String.valueOf(value)));
    }

    /**
     * The error message.
     * 
     * @param context
     *            The context.
     * @param value
     *            The value.
     * @return The message.
     * @throws PluginException
     *             On finder resume error.
     */
    protected String getMessage(IContext context, Object value) throws PluginException {
        return "Element " + getFinderInstance().resume(context) + " does not has an attribute '" + attribute + "' with value '" + value + "'.";
    }
}
