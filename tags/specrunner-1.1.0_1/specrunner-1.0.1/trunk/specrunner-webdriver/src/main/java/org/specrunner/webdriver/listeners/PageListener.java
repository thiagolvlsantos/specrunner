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
package org.specrunner.webdriver.listeners;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Element;
import nu.xom.ParentNode;

import org.openqa.selenium.WebDriver;
import org.specrunner.context.IContext;
import org.specrunner.dumper.impl.SourceDumperWritables;
import org.specrunner.listeners.IPluginListener;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.impl.UtilPlugin;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.webdriver.AbstractPluginBrowserAware;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.util.WritablePage;

/**
 * A listener to record screenshots after each step.
 * 
 * @author Thiago Santos
 * 
 */
public class PageListener implements IPluginListener {

    private static final Status DETAIL_STATUS = Status.newStatus("detail", false, -1);
    private final Map<String, Object> info = new HashMap<String, Object>();

    private final String name;
    private final IContext context;

    public PageListener(String name, IContext context) {
        this.name = name;
        this.context = context;
        info.put(SourceDumperWritables.LABEL_FIELD, "(" + name + ")");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IContext getContext() {
        return context;
    }

    @Override
    public void onBeforeInit(IContext context, IResultSet result) {
    }

    @Override
    public void onAfterInit(IContext context, IResultSet result) {
    }

    @Override
    public void onBeforeStart(IContext context, IResultSet result) {
    }

    @Override
    public void onAfterStart(IContext context, IResultSet result) {
    }

    @Override
    public void onBeforeEnd(IContext context, IResultSet result) {
    }

    @Override
    public void onAfterEnd(IContext context, IResultSet result) {
        IPlugin p = context.getPlugin();
        if (p instanceof AbstractPluginBrowserAware) {
            String tmp = (String) p.getParameter("name");
            String browserName = tmp != null ? tmp : PluginBrowser.BROWSER_NAME;
            if (name.equals(browserName) && this.context == context) {
                WebDriver client = (WebDriver) context.getByName(browserName);
                if (client == null) {
                    result.addResult(Status.FAILURE, context.peek(), "Browser instance named '" + browserName + "' not created. See PluginBrowser.");
                    return;
                }
                if (context.getNode() != null) {
                    ParentNode view = ((ParentNode) context.getNode());
                    Element ele = new Element("span");
                    UtilPlugin.setIgnore(ele);
                    view.appendChild(ele);
                    result.addResult(DETAIL_STATUS, context.newBlock(ele, p), new WritablePage(info, client));
                }
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PageListener other = (PageListener) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
