/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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

import java.util.LinkedList;
import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;

/**
 * Check select selected items.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginSelected extends AbstractPluginSelection {
    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    protected int checkSelection(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        Node node = context.getNode();
        Nodes expectedSelection = node.query("descendant::li | descendant::option");
        if (expectedSelection.size() == 0) {
            expectedSelection = new Nodes(node);
        }
        List<WebElement> tmp = element.findElements(By.xpath("descendant::option"));
        List<WebElement> currentSelection = new LinkedList<WebElement>();
        for (WebElement we : tmp) {
            if (we.isSelected()) {
                currentSelection.add(we);
            }
        }
        return testList(context, result, client, expectedSelection, currentSelection, false);
    }
}
