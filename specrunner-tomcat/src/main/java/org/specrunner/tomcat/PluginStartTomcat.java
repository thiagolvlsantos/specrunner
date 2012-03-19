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
