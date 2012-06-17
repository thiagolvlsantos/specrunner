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
package org.specrunner.htmlunit;

import java.lang.reflect.Constructor;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.htmlunit.listeners.PageListener;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.impl.AbstractPluginScoped;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReusableManager;
import org.specrunner.reuse.impl.AbstractReusable;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Cache;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebResponse;

/**
 * This plugin creates an HTML browser and add it to the test context with a
 * default name, or a given name. It means you can have more then one browser in
 * the same specification.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginBrowser extends AbstractPluginScoped {

    /**
     * Default browser name. To set a different name use the attribute
     * 'name=&lt;name&gt;'.
     */
    public static final String BROWSER_NAME = "browser";

    /**
     * Feature to set browser version.
     */
    public static final String FEATURE_VERSION = PluginBrowser.class.getName() + ".version";
    /**
     * The browser version.
     */
    private String version = "FIREFOX_3_6";

    /**
     * Feature to set host (for proxies).
     */
    public static final String FEATURE_HOST = PluginBrowser.class.getName() + ".host";
    /**
     * The host.
     */
    private String host;

    /**
     * Feature to set port (for proxies).
     */
    public static final String FEATURE_PORT = PluginBrowser.class.getName() + ".port";
    /**
     * The port.
     */
    private Integer port;

    /**
     * Feature to set user name, if the browser requires authentication.
     */
    public static final String FEATURE_USERNAME = PluginBrowser.class.getName() + ".username";
    /**
     * The username.
     */
    private String username;

    /**
     * Feature to set password, if the browser requires authentication.
     */
    public static final String FEATURE_PASSWORD = PluginBrowser.class.getName() + ".password";
    /**
     * The password.
     */
    private String password;

    /**
     * Set default browser http timeout.
     */
    public static final String FEATURE_HTTPTIMEOUT = PluginBrowser.class.getName() + ".httptimeout";
    /**
     * The browser http timeout. Default is '0'.
     */
    private Integer httptimeout = 0;

    /**
     * Feature to set browser connection type.
     */
    public static final String FEATURE_CONNECTION = PluginBrowser.class.getName() + ".connection";
    /**
     * <code>IWebConnection</code> type to be used.
     */
    private String connection;
    /**
     * Connection instance.
     */
    private Class<? extends IWebConnection> connectionType;

    /**
     * Default browser cache (class name) setting feature.
     */
    public static final String FEATURE_CACHE = PluginBrowser.class.getName() + ".cache";
    /**
     * <code>Cache</code> cache class to be used.
     */
    private String cache;
    /**
     * Cache instance.
     */
    private Cache cacheInstance;

    /**
     * Feature to set browser cached option (true/false).
     */
    public static final String FEATURE_CACHED = PluginBrowser.class.getName() + ".cached";
    /**
     * The cache status. Default is 'true'.
     */
    private Boolean cached = true;

    /**
     * Feature to enable browser recording.
     */
    public static final String FEATURE_RECORDING = PluginBrowser.class.getName() + ".recording";
    /**
     * Recording status. Default is "DEBUG log enabled".
     */
    private Boolean recording = UtilLog.LOG.isDebugEnabled();

    /**
     * Feature to set web driver reuse.
     */
    public static final String FEATURE_REUSE = PluginBrowser.class.getName() + ".reuse";
    /**
     * The reuse status.
     */
    private Boolean reuse = false;

    /**
     * Creates a browser.
     */
    public PluginBrowser() {
        setName(BROWSER_NAME);
    }

    /**
     * The browser version. Valid values are those in BrowserVersion class.
     * Default is "FIREFOX_3_6".
     * 
     * @return The HtmlUnit browser version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the browser version.
     * 
     * @param version
     *            The version.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the host for proxy settings. Default is null, to use a proxy set
     * 'host' an 'port' attributes.
     * 
     * @return The proxy host.
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the host.
     * 
     * @param host
     *            The host.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets the port for proxy settings. Default is null, to use a proxy set
     * 'host' and 'port' attributes.
     * 
     * @return The proxy port.
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the port.
     * 
     * @param port
     *            The port.
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Gets username for authentication. Default is null, to use authentication
     * set 'username' and 'password' attributes.
     * 
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * 
     * @param username
     *            The username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password for authentication. Default is null, to use
     * authentication set 'username' and 'password' attributes.
     * 
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * 
     * @param password
     *            The password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the default browser Http timeout. This timeout is passed to HtmlUnit
     * and it uses it as Socket connection time limitation. Default is '0',
     * which is infinite.
     * 
     * @return The timeout.
     */
    public Integer getHttptimeout() {
        return httptimeout;
    }

    /**
     * Sets the HTTP timeout.
     * 
     * @param httptimeout
     *            The timeout.
     */
    public void setHttptimeout(Integer httptimeout) {
        this.httptimeout = httptimeout;
    }

    /**
     * Gets the web connection type, valid values are classes which implements
     * <code>com.gargoylesoftware.htmlunit.WebConnection</code>. Default is
     * null.
     * 
     * @return The connection instance class.
     */
    public String getConnection() {
        return connection;
    }

    /**
     * Sets the connection class.
     * 
     * @param connection
     *            The connection.
     */
    public void setConnection(String connection) {
        this.connection = connection;
    }

    /**
     * Gets the cache type, valid values are classes which extend
     * <code>com.gargoylesoftware.htmlunit.Cache</code>. Default is null.
     * 
     * @return The cache class.
     */
    public String getCache() {
        return cache;
    }

    /**
     * Sets the cache class name.
     * 
     * @param cache
     *            The cache class name.
     */
    public void setCache(String cache) {
        this.cache = cache;
    }

    /**
     * Gets if the browser uses a cache strategy. Default is false, to enable
     * cache set 'cache' attribute to true.
     * 
     * @return The cache status.
     */
    public Boolean getCached() {
        return cached;
    }

    /**
     * Set cached version.
     * 
     * @param cached
     *            The cached status.
     */
    public void setCached(Boolean cached) {
        this.cached = cached;
    }

    /**
     * Gets if the browser will take a snapshot after each action or assertion.
     * Default is true.
     * 
     * @return The recording status.
     */
    public Boolean getRecording() {
        return recording;
    }

    /**
     * Set recording status.
     * 
     * @param recording
     *            The status.
     */
    public void setRecording(Boolean recording) {
        this.recording = recording;
    }

    /**
     * Gets the reuse status. If reuse is true, the browser can be reused by
     * multiple tests if the browser name is the same.
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
     *            The reuse status.
     */
    public void setReuse(Boolean reuse) {
        this.reuse = reuse;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        try {
            fh.set(FEATURE_VERSION, "version", String.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        if (host == null) {
            try {
                fh.set(FEATURE_HOST, "host", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
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
        if (username == null) {
            try {
                fh.set(FEATURE_USERNAME, "username", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (password == null) {
            try {
                fh.set(FEATURE_PASSWORD, "password", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        try {
            fh.set(FEATURE_HTTPTIMEOUT, "httptimeout", Integer.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        if (connection == null) {
            try {
                fh.set(FEATURE_CONNECTION, "connection", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (cache == null) {
            try {
                fh.set(FEATURE_CACHE, "cache", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (cached == null) {
            try {
                fh.set(FEATURE_CACHED, "cached", Boolean.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        try {
            fh.set(FEATURE_RECORDING, "recording", Boolean.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        try {
            fh.set(FEATURE_REUSE, "reuse", Boolean.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }

        if (connection != null) {
            try {
                connectionType = (Class<? extends IWebConnection>) Class.forName(connection);
            } catch (ClassNotFoundException e) {
                throw new PluginException("Connection class '" + connection + "' not found, or is not a subtype of " + WebConnection.class.getName() + ".", e);
            }
        }
        if (cache != null) {
            try {
                Class<? extends Cache> cacheType = (Class<? extends Cache>) Class.forName(cache);
                cacheInstance = cacheType.newInstance();
            } catch (ClassNotFoundException e) {
                throw new PluginException("Cache class '" + cache + "' not found, or is not a subtype of " + Cache.class.getName() + ".", e);
            } catch (Exception e) {
                throw new PluginException("Cache class '" + cache + "' instance could not be created.", e);
            }
        }
    }

    @Override
    @SuppressWarnings("serial")
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        try {
            IListenerManager fac = SpecRunnerServices.get(IListenerManager.class);
            if (recording) {
                fac.add(new PageListener(getName()));
            } else {
                fac.remove(getName());
            }
            IReusableManager reusables = SpecRunnerServices.get(IReusableManager.class);
            if (reuse) {
                Map<String, Object> cfg = new HashMap<String, Object>();
                cfg.put("version", version);
                cfg.put("name", getName());
                IReusable<?> reusable = reusables.get(getName());
                if (reusable != null && reusable.canReuse(cfg)) {
                    reusable.reset();
                    saveGlobal(context, getName(), reusable.getObject());
                    result.addResult(Success.INSTANCE, context.peek());
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Browser (" + getName() + ") reused.");
                    }
                    return ENext.DEEP;
                }
            }

            BrowserVersion bVersion = null;
            try {
                bVersion = (BrowserVersion) UtilEvaluator.evaluate(BrowserVersion.class.getName() + "." + version, context);
            } catch (Exception e) {
                throw new PluginException("The plugin version " + version + " is invalid.", e);
            }
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Browser named '" + getName() + "' version '" + bVersion.getNickname() + "'.");
            }
            final WebClient client = new WebClient(bVersion);

            // change web connection
            if (connectionType != null) {
                WebConnection connectionInstance = null;
                try {
                    Constructor<? extends WebConnection> constr = connectionType.getConstructor(WebClient.class);
                    connectionInstance = constr.newInstance(client);
                } catch (Exception e) {
                    connectionInstance = connectionType.newInstance();
                }
                client.setWebConnection(connectionInstance);
            }

            setDefaultClientBehaviors(client);

            // synchronize Ajax calls
            client.setAjaxController(new NicelyResynchronizingAjaxController());
            try {
                client.setUseInsecureSSL(true);
            } catch (GeneralSecurityException e) {
                throw new PluginException(e);
            }
            if (host != null && port != null) {
                ProxyConfig config = new ProxyConfig(host, port);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Browser named '" + getName() + "' proxy '" + host + ":" + port + "'.");
                }
                client.setProxyConfig(config);
            }
            if (username != null && password != null) {
                DefaultCredentialsProvider provider = new DefaultCredentialsProvider();
                provider.addCredentials(username, password);
                client.setCredentialsProvider(provider);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Browser named '" + getName() + "' credentials '" + username + "|" + password + "'.");
                }
            }
            if (cached || cacheInstance != null) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Browser named '" + getName() + "' cached.");
                }
                Cache cacheLocal = null;
                if (cacheInstance == null) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Using default cache.");
                    }
                    cacheLocal = new Cache() {
                        @Override
                        protected boolean isDynamicContent(WebResponse response) {
                            String type = response.getContentType();
                            boolean dynamic = ("".equals(type) || "text/html".equals(type)) && super.isDynamicContent(response);
                            if (UtilLog.LOG.isInfoEnabled()) {
                                String url = response.getWebRequest().getUrl().toString();
                                UtilLog.LOG.info("Dynamic '" + type + "' = " + dynamic + ", loaded in " + response.getLoadTime() + "mls, URL: " + url + ".");
                            }
                            return dynamic;
                        }
                    };
                } else {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Using customized cache '" + cacheInstance.getClass() + "'.");
                    }
                    cacheLocal = cacheInstance;
                }
                client.setCache(cacheLocal);
            } else {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Browser named '" + getName() + "' not cached.");
                }
            }

            client.setTimeout(httptimeout);
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Browser named '" + getName() + "' bound to '" + client + "'.");
            }
            saveGlobal(context, getName(), client);
            result.addResult(Success.INSTANCE, context.peek());
            if (reuse) {
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("WebClient reuse enabled.");
                }
                reusables.put(getName(), new AbstractReusable<WebClient>(getName(), client) {
                    @Override
                    public boolean canReuse(Map<String, Object> extra) {
                        Object localVersion = extra.get("version");
                        Object nameVersion = extra.get("name");
                        return localVersion != null && localVersion.equals(version) && nameVersion != null && nameVersion.equals(getName());
                    }

                    @Override
                    public void reset() {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("WebClient recycling '" + getName() + "'.");
                        }
                        client.closeAllWindows();
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("WebClient '" + getName() + "' windows reset.");
                        }
                    }

                    @Override
                    public void release() {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("WebClient '" + getName() + "' windows release.");
                        }
                    }
                });
            }
            return ENext.DEEP;
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }

    /**
     * Set common properties.
     * 
     * @param client
     *            The client to be set.
     */
    protected void setDefaultClientBehaviors(final WebClient client) {
        // print content on error
        client.setPrintContentOnFailingStatusCode(true);
        client.setThrowExceptionOnFailingStatusCode(false);
        client.setThrowExceptionOnScriptError(false);
    }
}