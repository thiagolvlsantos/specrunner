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
package org.specrunner.htmlunit.actions;

import java.io.IOException;

import org.specrunner.context.IContext;
import org.specrunner.htmlunit.AbstractPluginBrowserAware;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;

import com.gargoylesoftware.htmlunit.History;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Plugin helper to interact with history.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginHistory extends AbstractPluginBrowserAware {

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    protected void doEnd(IContext context, IResultSet result, WebClient client) throws PluginException {
        try {
            doEnd(context, result, client, client.getCurrentWindow().getHistory());
            result.addResult(Success.INSTANCE, context.peek());
        } catch (IOException e) {
            result.addResult(Failure.INSTANCE, context.peek(), e);
        }
    }

    /**
     * Perform actions on history elements.
     * 
     * @param context
     *            The context.
     * @param result
     *            The result.
     * @param client
     *            The client.
     * @param history
     *            The history.
     * @throws IOException
     *             On interaction errors.
     */
    protected abstract void doEnd(IContext context, IResultSet result, WebClient client, History history) throws IOException;

}
