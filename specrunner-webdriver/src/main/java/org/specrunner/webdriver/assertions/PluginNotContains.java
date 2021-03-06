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
package org.specrunner.webdriver.assertions;

import org.openqa.selenium.WebDriver;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.webdriver.IWait;
import org.specrunner.webdriver.impl.WaitDelegator;

/**
 * Check if body or a given element does not contains a text.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginNotContains extends PluginContains {

    @Override
    public void setIwait(final IWait iwait) {
        this.iwait = new WaitDelegator(iwait) {
            @Override
            public boolean isWaitForClient(IContext context, IResultSet result, WebDriver client) {
                return iwait.getWaitfor() != null;
            }
        };
    }

    @Override
    protected boolean test(String content, String value) {
        return !content.contains(value);
    }

    @Override
    protected String getMessage(IContext context, String value) throws PluginException {
        return getFinderInstance().resume(context) + " contains '" + value + "'.";
    }
}
