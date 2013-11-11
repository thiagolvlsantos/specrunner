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
package org.specrunner.htmlunit.listeners;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Element;
import nu.xom.ParentNode;

import org.specrunner.context.IContext;
import org.specrunner.dumper.core.SourceDumperWritables;
import org.specrunner.htmlunit.AbstractPluginBrowserAware;
import org.specrunner.htmlunit.PluginBrowser;
import org.specrunner.htmlunit.util.WritablePage;
import org.specrunner.listeners.core.AbstractPluginListener;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Detail;
import org.specrunner.result.status.Failure;
import org.specrunner.util.xom.UtilNode;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * A listener to record screenshots after each step.
 * 
 * @author Thiago Santos
 * 
 */
public class PageListener extends AbstractPluginListener {

    /**
     * Map of extra information.
     */
    private final Map<String, Object> info = new HashMap<String, Object>();

    /**
     * The name to be used in label.
     */
    private final String name;

    /**
     * Creates a listener with a name.
     * 
     * @param name
     *            The name.
     */
    public PageListener(String name) {
        this.name = name;
        info.put(SourceDumperWritables.LABEL_FIELD, "(" + name + ")");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        // nothing
    }

    @Override
    public void onAfterEnd(IPlugin plugin, IContext context, IResultSet result) {
        IPlugin p = context.getPlugin();
        if (p instanceof AbstractPluginBrowserAware) {
            String tmp = (String) p.getParameters().getParameter("name");
            String browserName = tmp != null ? tmp : PluginBrowser.BROWSER_NAME;
            if (name.equals(browserName)) {
                WebClient client = (WebClient) context.getByName(browserName);
                if (client == null) {
                    result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Browser instance named '" + browserName + "' not created. See PluginBrowser."));
                    return;
                }
                if (context.getNode() != null) {
                    ParentNode view = ((ParentNode) context.peek().getNode());
                    Element ele = new Element("span");
                    UtilNode.setIgnore(ele);
                    view.appendChild(ele);
                    result.addResult(Detail.INSTANCE, context.newBlock(ele, p), new WritablePage(info, client.getCurrentWindow().getEnclosedPage()));
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
