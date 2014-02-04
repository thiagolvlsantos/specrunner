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

import nu.xom.Node;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.webdriver.AbstractPluginFindSingle;

/**
 * Compare strings.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginCompare extends AbstractPluginFindSingle implements IAssertion {

    @Override
    protected void process(IContext context, IResultSet result, WebDriver client, WebElement element) throws PluginException {
        Object tmp = getValue(getValue() != null ? getValue() : context.getNode().getValue(), true, context);
        String expected = String.valueOf(tmp);
        String received = element.getText();
        Node node = context.getNode();
        PluginCompareUtils.compare(getNormalized(expected), getNormalized(received), context.newBlock(node, this), context, result, client);
    }
}