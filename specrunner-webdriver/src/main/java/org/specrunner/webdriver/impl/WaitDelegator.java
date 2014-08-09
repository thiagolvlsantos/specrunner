/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.webdriver.impl;

import org.openqa.selenium.WebDriver;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.webdriver.AbstractPluginBrowserAware;
import org.specrunner.webdriver.IWait;

/**
 * Wait delegator.
 * 
 * @author Thiago Santos
 * 
 */
public class WaitDelegator implements IWait {

    /**
     * Target.
     */
    private IWait delegate;

    /**
     * Default constructor.
     * 
     * @param delegate
     *            A wait algorithm.
     */
    public WaitDelegator(IWait delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isWaitForClient(AbstractPluginBrowserAware plugin, IContext context, IResultSet result, WebDriver client) {
        return delegate.isWaitForClient(plugin, context, result, client);
    }

    @Override
    public void waitForClient(AbstractPluginBrowserAware plugin, IContext context, IResultSet result, WebDriver client) throws PluginException {
        delegate.waitForClient(plugin, context, result, client);
    }

    @Override
    public String getWaitfor(AbstractPluginBrowserAware plugin, IContext context, IResultSet result, WebDriver client) throws PluginException {
        return delegate.getWaitfor(plugin, context, result, client);
    }
}