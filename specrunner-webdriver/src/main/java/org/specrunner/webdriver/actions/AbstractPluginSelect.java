/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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

import nu.xom.Node;
import nu.xom.Nodes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritableFactoryManager;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.webdriver.AbstractPluginFindSingle;

/**
 * Perform some action over select elements.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginSelect extends AbstractPluginFindSingle {

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        if (!isSelect(element)) {
            result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Element " + getFinderInstance().resume(context) + " is not a select is " + element.getClass().getName()), SRServices.get(IWritableFactoryManager.class).get(WebDriver.class).newWritable(client));
        } else {
            Node node = context.getNode();
            Nodes nodes = node.query(getOptionsPath());
            if (nodes.size() == 0) {
                nodes = new Nodes(node);
            }
            List<WebElement> options = element.findElements(By.xpath("descendant::option"));
            int errors = 0;
            for (int i = 0; i < nodes.size(); i++) {
                Node current = nodes.get(i);
                String option = getNormalized(current.getValue());
                boolean success = false;
                for (WebElement o : options) {
                    if (option.equals(getNormalized(o.getText()))) {
                        doSomething(element, o);
                        success = true;
                        break;
                    }
                }
                if (!success) {
                    result.addResult(Failure.INSTANCE, context.newBlock(current, this), new PluginException("The option '" + option + "' is missing."));
                    errors++;
                }
            }
            if (errors > 0) {
                result.addResult(Failure.INSTANCE, context.newBlock(node, this), new PluginException(errors + " option(s) is(are) missing."), SRServices.get(IWritableFactoryManager.class).get(WebDriver.class).newWritable(client));
            } else {
                result.addResult(Success.INSTANCE, context.newBlock(node, this));
            }
        }
    }

    /**
     * Says if the element is a select.
     * 
     * @param element
     *            The element.
     * @return true, if is select, false, otherwise.
     */
    protected boolean isSelect(WebElement element) {
        return element.getTagName().equalsIgnoreCase("select");
    }

    /**
     * The XPath for options.
     * 
     * @return The XPath.
     */
    protected String getOptionsPath() {
        return "descendant::li | descendant::option";
    }

    /**
     * Perform something on select option.
     * 
     * @param element
     *            The select.
     * @param option
     *            The option.
     */
    protected abstract void doSomething(WebElement element, WebElement option);
}
