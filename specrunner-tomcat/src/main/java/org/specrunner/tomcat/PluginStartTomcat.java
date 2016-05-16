/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginScoped;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.reuse.core.AbstractReusable;
import org.specrunner.util.UtilLog;

/**
 * Start a Tomcat server.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginStartTomcat extends AbstractPluginScoped {

    /**
     * Default tomcat name.
     */
    public static final String SERVER_NAME = "tomcatName";
    /**
     * Lock object used to guarantee port exclusion on multi-thread executions.
     */
    private static Object lock = new Object();

    /**
     * Feature to set tomcat port.
     */
    public static final String FEATURE_PORT = PluginStartTomcat.class.getName() + ".port";
    /**
     * The port to be used.
     */
    private Integer port;

    /**
     * Feature to set tomcat basedir.
     */
    public static final String FEATURE_BASEDIR = PluginStartTomcat.class.getName() + ".basedir";
    /**
     * The base directory.
     */
    private String basedir;

    /**
     * Feature to set tomcat context.
     */
    public static final String FEATURE_CONTEXT = PluginStartTomcat.class.getName() + ".context";
    /**
     * The application context.
     */
    private String context;

    /**
     * Feature to set tomcat war.
     */
    public static final String FEATURE_WAR = PluginStartTomcat.class.getName() + ".war";
    /**
     * The war directory.
     */
    private String war;

    /**
     * Feature to enable tomcat allocate a dynamic port.
     */
    public static final String FEATURE_DYNAMIC = PluginStartTomcat.class.getName() + ".dynamic";
    /**
     * Set dynamic start up. Default is 'true'.
     */
    private Boolean dynamic = true;

    /**
     * Feature to set reusable "tomcats".
     */
    public static final String FEATURE_REUSE = PluginStartTomcat.class.getName() + ".reuse";
    /**
     * The reuse status. Default is 'false'.
     */
    private Boolean reuse = false;

    /**
     * Default constructor.
     */
    public PluginStartTomcat() {
        setName(SERVER_NAME);
    }

    /**
     * Get base directory.
     * 
     * @return The base directory.
     */
    public String getBasedir() {
        return basedir;
    }

    /**
     * Set the base directory.
     * 
     * @param basedir
     *            The base directory.
     */
    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    /**
     * The server port.
     * 
     * @return The port.
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Set the server port.
     * 
     * @param port
     *            The port.
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Get the application context.
     * 
     * @return The context.
     */
    public String getContext() {
        return context;
    }

    /**
     * Set the application context.
     * 
     * @param context
     *            The application context.
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * Get the WAR.
     * 
     * @return The war.
     */
    public String getWar() {
        return war;
    }

    /**
     * Set the WAR.
     * 
     * @param war
     *            The war.
     */
    public void setWar(String war) {
        this.war = war;
    }

    /**
     * Get dynamic status.
     * 
     * @return true, if dynamic port allocation is enable, false, otherwise.
     */
    public Boolean getDynamic() {
        return dynamic;
    }

    /**
     * Set dynamic mode.
     * 
     * @param dynamic
     *            The dynamic status.
     */
    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    /**
     * Get reusable server.
     * 
     * @return The reuse status.
     */
    public Boolean getReuse() {
        return reuse;
    }

    /**
     * Set reuse status.
     * 
     * @param reuse
     *            true, for reuse, false otherwise.
     */
    public void setReuse(Boolean reuse) {
        this.reuse = reuse;
    }

    @Override
    public ActionType getActionType() {
        return Assertion.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        if (basedir == null) {
            fm.set(FEATURE_BASEDIR, this);
        }
        if (port == null) {
            fm.set(FEATURE_PORT, this);
        }
        if (context == null) {
            fm.set(FEATURE_CONTEXT, this);
        }
        if (war == null) {
            fm.set(FEATURE_WAR, this);
        }
        fm.set(FEATURE_DYNAMIC, this);
        fm.set(FEATURE_REUSE, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        synchronized (lock) {
            try {
                IReuseManager reusables = SRServices.get(IReuseManager.class);
                if (reuse) {
                    Map<String, Object> cfg = new HashMap<String, Object>();
                    cfg.put("name", getName());
                    cfg.put("basedir", basedir);
                    cfg.put("port", port);
                    cfg.put("context", getContext());
                    cfg.put("war", getWar());
                    IReusable<?> reusable = reusables.get(getName());
                    if (reusable != null && reusable.canReuse(cfg)) {
                        reusable.reset();
                        saveGlobal(context, getName(), reusable.getObject());
                        result.addResult(Success.INSTANCE, context.peek());
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Tomcat (" + getName() + "/" + reusable.getObject() + ") reused.");
                        }
                        return ENext.DEEP;
                    }
                }
                final Tomcat server = createServer();

                if (dynamic) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Jetty dynamic port lookup.");
                    }
                    scanAvailablePort(server);
                }

                waitForStart(server);

                saveGlobal(context, getName(), server);
                if (reuse) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Tomcat reuse enabled.");
                    }
                    reusables.put(getName(), new AbstractReusable<Tomcat>(getName(), server) {
                        @Override
                        public boolean canReuse(Map<String, Object> cfg) {
                            Object tmpName = cfg.get("name");
                            Object tmpBasedir = cfg.get("basedir");
                            Object tmpPort = cfg.get("port");
                            Object tmpContext = cfg.get("context");
                            Object tmpWar = cfg.get("war");
                            return tmpName != null && tmpName.equals(PluginStartTomcat.this.getName()) && tmpBasedir != null && tmpBasedir.equals(getBasedir()) && tmpPort != null && tmpPort.equals(getPort()) && tmpContext != null && tmpContext.equals(getContext()) && tmpWar != null && tmpWar.equals(getWar());
                        }

                        @Override
                        public void reset() {
                        }

                        @Override
                        public void release() {
                            try {
                                server.stop();
                                if (UtilLog.LOG.isDebugEnabled()) {
                                    UtilLog.LOG.debug("Tomcat stopped.");
                                }
                            } catch (LifecycleException e) {
                                if (UtilLog.LOG.isDebugEnabled()) {
                                    UtilLog.LOG.debug("Tomcat stopped with fail.", e);
                                }
                            }
                        }
                    });
                }
                result.addResult(Success.INSTANCE, context.peek());
                if (!reuse) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Tomcat started and ready.");
                    }
                }
                context.saveGlobal(getName(), server);
            } catch (Exception e) {
                throw new PluginException(e);
            }
        }
        return ENext.DEEP;
    }

    /**
     * Creates the server from configuration file.
     * 
     * @return The server.
     */
    protected Tomcat createServer() {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(getBasedir());
        tomcat.setPort(getPort());
        Context c = tomcat.addWebapp(null, "/" + getContext(), new File(getWar()).getAbsolutePath());
        c.setReloadable(true);
        return tomcat;
    }

    /**
     * Scan for a available port to set into server.
     * 
     * @param server
     *            The server.
     * @throws PluginException
     *             On setting errors.
     */
    protected void scanAvailablePort(final Tomcat server) throws PluginException {
        boolean available = false;
        final int tries = 1000;
        for (int i = port; !available && i < port + tries; i++) {
            InputStream in = null;
            Socket sock = null;
            try {
                sock = new Socket("localhost", i);
                in = sock.getInputStream();
            } catch (Exception e) {
                port = i;
                available = true;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {
                        if (UtilLog.LOG.isTraceEnabled()) {
                            UtilLog.LOG.trace(e.getMessage(), e);
                        }
                    }
                }
                if (sock != null) {
                    try {
                        sock.close();
                    } catch (Exception e) {
                        if (UtilLog.LOG.isTraceEnabled()) {
                            UtilLog.LOG.trace(e.getMessage(), e);
                        }
                    }
                }
            }
        }
        if (!available) {
            throw new PluginException("No available port from '" + (port - tries) + "' to '" + port + "'.");
        }
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Tomcat port '" + port + "' available.");
        }
        server.setPort(port);
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Tomcat port set to '" + port + "'.");
        }
    }

    /**
     * Start an wait for server start.
     * 
     * @param server
     *            The server.
     * @throws Exception
     *             On initialization errors.
     */
    protected void waitForStart(Tomcat server) throws Exception {
        server.start();
    }
}
