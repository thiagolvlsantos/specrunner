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
package org.specrunner.jetty;

import org.eclipse.jetty.server.Server;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginNamed;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.util.UtilLog;

/**
 * Stop a jetty server.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginStopJetty extends AbstractPluginNamed {

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        String tmp = getName() != null ? getName() : PluginStartJetty.SERVER_NAME;
        Server server = (Server) context.getByName(tmp);
        if (server == null) {
            result.addResult(Failure.INSTANCE, context.peek(), "Server instance named '" + tmp + "' not found. See PluginStartJetty.");
            return ENext.DEEP;
        }
        try {
            server.stop();
            result.addResult(Success.INSTANCE, context.peek());
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Jetty finished.");
            }
        } catch (Exception e) {
            throw new PluginException(e);
        }
        return ENext.DEEP;
    }
}
