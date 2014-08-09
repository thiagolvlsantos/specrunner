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
package org.specrunner.htmlunit;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Something that waits for actions.
 * 
 * @author Thiago Santos
 * 
 */
public interface IWait {

    /**
     * Sign actions to wait for browser response.
     * 
     * @param plugin
     *            Source plugin.
     * @param context
     *            The test context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * 
     * @return true, when wait is desired, false, otherwise. Default is true.
     */
    boolean isWaitForClient(AbstractPluginBrowserAware plugin, IContext context, IResultSet result, WebClient client);

    /**
     * Wait for client. If wait is set it already has waited for the period time
     * set.
     * 
     * @param plugin
     *            Source plugin.
     * @param context
     *            The test context.
     * @param result
     *            The result.
     * 
     * @param client
     *            The client.
     * @throws PluginException
     *             On wait for client errors.
     */
    void waitForClient(AbstractPluginBrowserAware plugin, IContext context, IResultSet result, WebClient client) throws PluginException;
}