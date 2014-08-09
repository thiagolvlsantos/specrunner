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
package org.specrunner.webdriver.actions;

import org.openqa.selenium.WebDriver;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.webdriver.AbstractPluginBrowserAware;
import org.specrunner.webdriver.AbstractPluginUrlAware;
import org.specrunner.webdriver.IWait;
import org.specrunner.webdriver.impl.WaitDelegator;

/**
 * Quits the driver.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginQuit extends AbstractPluginUrlAware {

    /**
     * Override delegate attribute.
     */
    private IWait local;

    @Override
    public void setIwait(IWait iwait) {
        this.iwait = new WaitDelegator(iwait) {
            @Override
            public boolean isWaitForClient(AbstractPluginBrowserAware plugin, IContext context, IResultSet result, WebDriver client) {
                return false;
            }
        };
    }

    @Override
    public IWait getIwait() {
        return local;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebDriver client) throws PluginException {
        // only not reusable browser can be quit.
        if (SRServices.get(IReuseManager.class).get(getBrowserName()) == null) {
            client.quit();
        }
        result.addResult(Success.INSTANCE, context.peek());
    }

}