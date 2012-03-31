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
package org.specrunner.tomcat;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginNamed;
import org.specrunner.result.IResultSet;

public class PluginStartTomcat extends AbstractPluginNamed {

    private String basedir;
    private int port;
    private String context;
    private String war;

    public String getBasedir() {
        return basedir;
    }

    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getWar() {
        return war;
    }

    public void setWar(String war) {
        this.war = war;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(getBasedir());
        tomcat.setPort(getPort());
        Context c = tomcat.addWebapp(null, "/" + getContext(), new File(getWar()).getAbsolutePath());
        c.setReloadable(true);
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            throw new PluginException(e);
        }
        context.saveGlobal("tomcatName", tomcat);
        return ENext.DEEP;
    }
}
