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

import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.webdriver.AbstractPluginFindSingle;
import org.specrunner.webdriver.util.WritablePage;

/**
 * Select/unselect a given element, or a list of elements in &lt;ul&gt;.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginSelect extends AbstractPluginFindSingle implements IAction {

    private Boolean unselect;

    public Boolean getUnselect() {
        return unselect;
    }

    public void setUnselect(Boolean unselect) {
        this.unselect = unselect;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        if (!isSelect(element)) {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Element " + getFinder().resume(context) + " is not a select is " + element.getClass().getName()), new WritablePage(client));
        } else {
            Node node = context.getNode();
            Nodes nodes = node.query("descendant::li");
            if (nodes.size() == 0) {
                nodes = new Nodes(node);
            }
            List<WebElement> options = element.findElements(By.xpath("descendant::option"));
            int errors = 0;
            for (int i = 0; i < nodes.size(); i++) {
                Node current = nodes.get(i);
                String option = current.getValue();
                boolean success = false;
                for (WebElement o : options) {
                    if (option.equals(o.getText())) {
                        if (unselect != null && unselect) {
                            if (o.isSelected()) {
                                o.click();
                            }
                        } else {
                            if (!o.isSelected()) {
                                o.click();
                            }
                        }
                        success = true;
                        break;
                    }
                }
                if (!success) {
                    result.addResult(Status.FAILURE, context.newBlock(current, this), new PluginException("The option '" + option + "' is missing."));
                    errors++;
                }
            }
            if (errors > 0) {
                result.addResult(Status.FAILURE, context.newBlock(node, this), new PluginException(errors + " option(s) is(are) missing."), new WritablePage(client));
            } else {
                result.addResult(Status.SUCCESS, context.newBlock(node, this));
            }
        }
    }

    protected boolean isSelect(WebElement element) {
        return element.getTagName().equalsIgnoreCase("select");
    }
}