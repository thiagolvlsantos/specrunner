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
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginScoped;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReusableManager;
import org.specrunner.reuse.impl.AbstractReusable;
import org.specrunner.util.UtilLog;

public class PluginStartJetty extends AbstractPluginScoped {

    public static final String JETTY_NAME = "jettyName";
    private static Object lock = new Object();

    public static final String FEATURE_FILE = PluginStartJetty.class.getName() + ".file";
    private String file;

    public static final String FEATURE_DYNAMIC = PluginStartJetty.class.getName() + ".dynamic";
    private Boolean dynamic = true;

    public static final String FEATURE_PORT = PluginStartJetty.class.getName() + ".port";
    private Integer port;

    public static final String FEATURE_REUSE = PluginStartJetty.class.getName() + ".reuse";
    private Boolean reuse = false;

    public PluginStartJetty() {
        setName(JETTY_NAME);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Boolean getDynamic() {
        return dynamic;
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getReuse() {
        return reuse;
    }

    public void setReuse(Boolean reuse) {
        this.reuse = reuse;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        if (file == null) {
            try {
                fh.set(FEATURE_FILE, "file", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        try {
            fh.set(FEATURE_DYNAMIC, "dynamic", Boolean.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        if (port == null) {
            try {
                fh.set(FEATURE_PORT, "port", Integer.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        try {
            fh.set(FEATURE_REUSE, "reuse", Boolean.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        synchronized (lock) {
            try {
                Log.getRootLogger().setDebugEnabled(true);
                if (file == null) {
                    throw new PluginException("Jetty file name must be set using attribute 'file'.");
                }

                IReusableManager reusables = SpecRunnerServices.get(IReusableManager.class);
                if (reuse) {
                    Map<String, Object> cfg = new HashMap<String, Object>();
                    cfg.put(getFileForJettyName(getName()), file);
                    IReusable reusable = reusables.get(getName());
                    if (reusable != null && reusable.canReuse(cfg)) {
                        reusable.reset();
                        saveGlobal(context, getName(), reusable.getObject());
                        result.addResult(Status.SUCCESS, context.peek());
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Jetty (" + getName() + "/" + Server.getVersion() + ") with " + file + " reused.");
                        }
                        return;
                    }
                }

                InputStream in = PluginStartJetty.class.getResourceAsStream(file);
                if (in == null) {
                    throw new PluginException("Jetty file '" + file + "' not found.");
                }
                XmlConfiguration configuration = new XmlConfiguration(in);
                in.close();
                final Server s = (Server) configuration.configure();

                // cannot shutdown JVM, leave it to test programmer.
                ShutdownHandler sdh = s.getChildHandlerByClass(ShutdownHandler.class);
                if (sdh != null) {
                    sdh.setExitJvm(false);
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Jetty System.exit(..) disabled.");
                    }
                }

                // for reusable jetty the session manager must invalidate all
                // previous sessions
                LocalSessionManager sm = null;
                if (reuse) {
                    WebAppContext web = s.getChildHandlerByClass(WebAppContext.class);
                    if (web != null) {
                        SessionHandler h = web.getSessionHandler();
                        sm = new LocalSessionManager();
                        h.setSessionManager(sm);
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Jetty LocalSessionManager added -> " + sm + ".");
                        }
                    }
                }

                for (Connector c : s.getConnectors()) {
                    if (c instanceof SelectChannelConnector) {
                        port = c.getPort();
                        break;
                    }
                }
                if (dynamic) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Jetty dynamic port lookup.");
                    }
                    boolean available = false;
                    final int tries = 1000;
                    for (int i = port; !available && i < port + tries; i++) {
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
                    for (Connector c : s.getConnectors()) {
                        if (c instanceof SelectChannelConnector) {
                            c.setPort(port);
                            if (UtilLog.LOG.isInfoEnabled()) {
                                UtilLog.LOG.info("Jetty port set to '" + port + "'.");
                            }
                            break;
                        }
                    }
                }

                s.start();
                while (!s.isRunning()) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Waiting Jetty start.");
                    }
                }
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Jetty version -> " + Server.getVersion() + ".");
                }
                saveGlobal(context, getName(), s);
                if (reuse) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Jetty reuse enabled.");
                    }
                    final LocalSessionManager localSm = sm;
                    reusables.put(getName(), new AbstractReusable(getName(), s) {
                        @Override
                        public boolean canReuse(Map<String, Object> extra) {
                            Object obj = extra.get(getFileForJettyName(getName()));
                            return obj != null && obj.equals(file);
                        }

                        @Override
                        public void reset() {
                            if (UtilLog.LOG.isInfoEnabled()) {
                                UtilLog.LOG.info("Jetty recycling '" + s + "'.");
                            }
                            for (Connector c : s.getConnectors()) {
                                if (c instanceof SelectChannelConnector) {
                                    if (UtilLog.LOG.isInfoEnabled()) {
                                        UtilLog.LOG.info("Jetty port listening on '" + c.getPort() + "'.");
                                    }
                                    break;
                                }
                            }
                            if (localSm != null) {
                                localSm.invalidateSessions();
                            }
                            s.clearAttributes();
                            if (UtilLog.LOG.isInfoEnabled()) {
                                UtilLog.LOG.info("Jetty '" + getName() + "' attributes cleared.");
                            }
                        }

                        @Override
                        public void release() {
                            try {
                                s.stop();
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
                    });
                }
                result.addResult(Status.SUCCESS, context.peek());
                if (!reuse) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Jetty started and ready.");
                    }
                }
            } catch (Exception e) {
                throw new PluginException(e);
            }
        }
    }

    protected String getFileForJettyName(String jettyName) {
        return "file_" + jettyName;
    }

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
}