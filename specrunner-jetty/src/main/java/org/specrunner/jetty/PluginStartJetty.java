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

import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginScoped;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.reuse.impl.AbstractReusable;
import org.specrunner.util.UtilLog;

/**
 * Starts a jetty server based on a <code>jetty.xml</code> file.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginStartJetty extends AbstractPluginScoped {

    /**
     * Default server name.
     */
    public static final String SERVER_NAME = "jettyName";
    /**
     * Lock object used to guarantee port exclusion on multi-thread executions.
     */
    private static Object lock = new Object();

    /**
     * Feature to set configuration file.
     */
    public static final String FEATURE_FILE = PluginStartJetty.class.getName() + ".file";
    /**
     * The file.
     */
    private String file;

    /**
     * Feature to enable server allocate a dynamic port.
     */
    public static final String FEATURE_DYNAMIC = PluginStartJetty.class.getName() + ".dynamic";
    /**
     * Set dynamic start up. Default is 'true'.
     */
    private Boolean dynamic = true;

    /**
     * Feature to set server port.
     */
    public static final String FEATURE_PORT = PluginStartJetty.class.getName() + ".port";
    /**
     * The port to be used.
     */
    private Integer port;

    /**
     * Feature to set reusable "jetties".
     */
    public static final String FEATURE_REUSE = PluginStartJetty.class.getName() + ".reuse";
    /**
     * The reuse status. Default is 'false'.
     */
    private Boolean reuse = false;

    /**
     * Feature to set class loader.
     */
    public static final String FEATURE_CLASSLOADER = PluginStartJetty.class.getName() + ".classloader";
    /**
     * The class loader adjust to current Thread. Default is 'true'.
     */
    private Boolean classloader = true;

    /**
     * Default constructor.
     */
    public PluginStartJetty() {
        setName(SERVER_NAME);
    }

    /**
     * Gets the configuration file.
     * 
     * @return The configuration file.
     */
    public String getFile() {
        return file;
    }

    /**
     * Set the configuration file.
     * 
     * @param file
     *            The file.
     */
    public void setFile(String file) {
        this.file = file;
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
     * Get port value.
     * 
     * @return The port value.
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Set expected port.
     * 
     * @param port
     *            The port.
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Get reusable Jetty.
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

    /**
     * Get the classloader setup.
     * 
     * @return The classloader state.
     */
    public Boolean getClassloader() {
        return classloader;
    }

    /**
     * Enable the revertion of classloader to current thread.
     * 
     * @param classloader
     *            The classloader flag.
     */
    public void setClassloader(Boolean classloader) {
        this.classloader = classloader;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SpecRunnerServices.getFeatureManager();
        if (file == null) {
            fm.set(FEATURE_FILE, this);
        }
        fm.set(FEATURE_DYNAMIC, this);
        if (port == null) {
            fm.set(FEATURE_PORT, this);
        }
        fm.set(FEATURE_REUSE, this);
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        synchronized (lock) {
            try {
                Log.getRootLogger().setDebugEnabled(true);
                if (file == null) {
                    throw new PluginException("Jetty file name must be set using attribute 'file'.");
                }

                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Jetty version -> " + Server.getVersion() + ".");
                }

                IReuseManager reusables = SpecRunnerServices.get(IReuseManager.class);
                if (reuse) {
                    Map<String, Object> cfg = new HashMap<String, Object>();
                    cfg.put(getFileForJettyName(getName()), file);
                    IReusable<?> reusable = reusables.get(getName());
                    if (reusable != null && reusable.canReuse(cfg)) {
                        reusable.reset();
                        saveGlobal(context, getName(), reusable.getObject());
                        result.addResult(Success.INSTANCE, context.peek());
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Jetty (" + getName() + "/" + Server.getVersion() + ") with " + file + " reused.");
                        }
                        return;
                    }
                }

                final Server server = createServer();

                LocalSessionManager sm = setSessionManager(server);

                if (dynamic) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Jetty dynamic port lookup.");
                    }
                    scanAvailablePort(server);
                }

                perform(server);

                waitForStart(server);

                saveGlobal(context, getName(), server);
                if (reuse) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Jetty reuse enabled.");
                    }
                    reusables.put(getName(), new ReusableJetty(getName(), server, sm));
                }
                result.addResult(Success.INSTANCE, context.peek());
                if (!reuse && UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Jetty started and ready.");
                }
            } catch (Exception e) {
                throw new PluginException(e);
            }
        }
    }

    /**
     * Creates the server from configuration file.
     * 
     * @return The server.
     * @throws Exception
     *             On creation errors.
     */
    protected Server createServer() throws Exception {
        InputStream config = PluginStartJetty.class.getResourceAsStream(file);
        if (config == null) {
            throw new PluginException("Jetty file '" + file + "' not found.");
        }
        XmlConfiguration configuration = new XmlConfiguration(config);
        config.close();
        final Server server = (Server) configuration.configure();
        setShutdownManager(server);
        getPortFromServer(server);
        setClassloader(server);
        return server;
    }

    /**
     * Set shutdown manager.
     * 
     * @param server
     *            The server
     */
    protected void setShutdownManager(final Server server) {
        // cannot shutdown JVM, leave it to test programmer.
        ShutdownHandler sdh = server.getChildHandlerByClass(ShutdownHandler.class);
        if (sdh != null) {
            sdh.setExitJvm(false);
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Jetty System.exit(..) disabled.");
            }
        }
    }

    /**
     * Recover port information from a server previously created.
     * 
     * @param server
     *            The server.
     */
    protected void getPortFromServer(final Server server) {
        for (Connector c : server.getConnectors()) {
            if (c instanceof SelectChannelConnector) {
                port = c.getPort();
                break;
            }
        }
    }

    /**
     * Set Jetty classloader.
     * 
     * @param server
     *            The server.
     */
    protected void setClassloader(final Server server) {
        WebAppContext web = server.getChildHandlerByClass(WebAppContext.class);
        if (web != null && classloader) {
            web.setClassLoader(Thread.currentThread().getContextClassLoader());
        }
    }

    /**
     * Sets the session manager. for reusable server the session manager must
     * invalidate all previous sessions.
     * 
     * @param server
     *            The server.
     * @return The session manager used.
     */
    protected LocalSessionManager setSessionManager(final Server server) {
        LocalSessionManager sm = null;
        if (reuse) {
            WebAppContext web = server.getChildHandlerByClass(WebAppContext.class);
            if (web != null) {
                SessionHandler h = web.getSessionHandler();
                sm = new LocalSessionManager();
                h.setSessionManager(sm);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Jetty LocalSessionManager added -> " + sm + ".");
                }
            }
        }
        return sm;
    }

    /**
     * Scan for a available port to set into server.
     * 
     * @param server
     *            The server.
     * @throws PluginException
     *             On setting errors.
     */
    protected void scanAvailablePort(final Server server) throws PluginException {
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
            UtilLog.LOG.info("Jetty port '" + port + "' available.");
        }
        for (Connector c : server.getConnectors()) {
            if (c instanceof SelectChannelConnector) {
                c.setPort(port);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Jetty port set to '" + port + "'.");
                }
                break;
            }
        }
    }

    /**
     * Hook for customized setups.
     * 
     * @param server
     *            The server object.
     */
    public void perform(Server server) {
        // perform something.
    }

    /**
     * Start an wait for server start.
     * 
     * @param server
     *            The server.
     * @throws Exception
     *             On initialization errors.
     */
    protected void waitForStart(final Server server) throws Exception {
        server.start();
        while (!server.isRunning()) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Waiting Jetty start.");
            }
        }
    }

    /**
     * Gets Jetty file for a given Jetty.
     * 
     * @param jettyName
     *            The Jetty name.
     * @return The configuration file.
     */
    protected String getFileForJettyName(String jettyName) {
        return "file_" + jettyName;
    }

    /**
     * A implementation that exposes the <code>invalidateSessions</code> method.
     * 
     * @author Thiago Santos
     * 
     */
    private final class LocalSessionManager extends HashSessionManager {
        @Override
        public void invalidateSessions() {
            try {
                super.invalidateSessions();
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Sessions removed.");
                }
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Reusable server resource.
     * 
     * @author Thiago Santos
     * 
     */
    protected final class ReusableJetty extends AbstractReusable<Server> {
        /**
         * Session manager.
         */
        private final LocalSessionManager sessionManager;

        /**
         * Creates a reusable server package.
         * 
         * @param name
         *            The server name.
         * @param server
         *            The server instance.
         * @param sessionManager
         *            The session manager.
         */
        protected ReusableJetty(String name, Server server, LocalSessionManager sessionManager) {
            super(name, server);
            this.sessionManager = sessionManager;
        }

        @Override
        public boolean canReuse(Map<String, Object> extra) {
            Object obj = extra.get(getFileForJettyName(getName()));
            return obj != null && obj.equals(file);
        }

        @Override
        public void reset() {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Jetty recycling '" + getObject() + "'.");
            }
            for (Connector c : getObject().getConnectors()) {
                if (c instanceof SelectChannelConnector) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Jetty port listening on '" + c.getPort() + "'.");
                    }
                    break;
                }
            }
            if (sessionManager != null) {
                sessionManager.invalidateSessions();
            }
            getObject().clearAttributes();
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Jetty '" + getName() + "' attributes cleared.");
            }
        }

        @Override
        public void release() {
            try {
                getObject().stop();
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            } finally {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Jetty '" + getName() + "' shutdown.");
                }
            }
        }
    }
}