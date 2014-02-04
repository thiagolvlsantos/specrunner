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
package org.specrunner.jetty;

import org.eclipse.jetty.server.Server;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginNamed;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.UtilLog;

public class PluginStopJetty extends AbstractPluginNamed {

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        String tmp = getName() != null ? getName() : PluginStartJetty.JETTY_NAME;
        Server server = (Server) context.getByName(tmp);
        if (server == null) {
            result.addResult(Status.FAILURE, context.peek(), "Server instance named '" + tmp + "' not found. See PluginStartJetty.");
            return;
        }
        try {
            server.stop();
            result.addResult(Status.SUCCESS, context.peek());
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Jetty finished.");
            }
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }
}
