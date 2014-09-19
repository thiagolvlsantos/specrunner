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
package org.specrunner.webdriver.impl;

import java.lang.reflect.Constructor;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.specrunner.SRServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.IParameterDecorator;
import org.specrunner.parameters.core.ParameterDecoratorImpl;
import org.specrunner.util.UtilLog;
import org.specrunner.webdriver.IHtmlUnitDriver;
import org.specrunner.webdriver.impl.htmlunit.IWebConnection;
import org.specrunner.webdriver.impl.htmlunit.OptimizedCssErrorHandler;
import org.specrunner.webdriver.impl.htmlunit.OptimizedIncorrectnessListener;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Cache;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebResponse;

/**
 * Implementation for HtmlUnitDriver which enable recovering WebClient instance
 * and other customizations like setting the connection handler.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class HtmlUnitDriverLocal extends HtmlUnitDriver implements IHtmlUnitDriver {

    /**
     * Parameter decorator.
     */
    private IParameterDecorator parameters;

    /**
     * Browser name.
     */
    private String name;

    /**
     * Feature to set host (for proxies).
     */
    public static final String FEATURE_HOST = HtmlUnitDriverLocal.class.getName() + ".host";
    /**
     * The host.
     */
    private String host;

    /**
     * Feature to set port (for proxies).
     */
    public static final String FEATURE_PORT = HtmlUnitDriverLocal.class.getName() + ".port";
    /**
     * The port.
     */
    private Integer port;

    /**
     * Feature to set user name, if the browser requires authentication.
     */
    public static final String FEATURE_USERNAME = HtmlUnitDriverLocal.class.getName() + ".username";
    /**
     * The username.
     */
    private String username;

    /**
     * Feature to set password, if the browser requires authentication.
     */
    public static final String FEATURE_PASSWORD = HtmlUnitDriverLocal.class.getName() + ".password";
    /**
     * The password.
     */
    private String password;

    /**
     * Set default browser http timeout.
     */
    public static final String FEATURE_HTTPTIMEOUT = HtmlUnitDriverLocal.class.getName() + ".httptimeout";
    /**
     * The browser http timeout. Default is '0'.
     */
    private Integer httptimeout = 0;

    /**
     * Feature to set browser connection type.
     */
    public static final String FEATURE_CONNECTION = HtmlUnitDriverLocal.class.getName() + ".connection";
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
    public static final String FEATURE_CACHE = HtmlUnitDriverLocal.class.getName() + ".cache";
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
    public static final String FEATURE_CACHED = HtmlUnitDriverLocal.class.getName() + ".cached";
    /**
     * The cache status. Default is 'true'.
     */
    private Boolean cached = true;

    /**
     * Default constructor.
     */
    public HtmlUnitDriverLocal() {
        super();
    }

    /**
     * Creates the client with JS enabled or not.
     * 
     * @param enableJavascript
     *            true, to enable java script, false, otherwise.
     */
    public HtmlUnitDriverLocal(boolean enableJavascript) {
        super(enableJavascript);
    }

    /**
     * Creates a client with a given version.
     * 
     * @param version
     *            The browser version.
     */
    public HtmlUnitDriverLocal(BrowserVersion version) {
        super(version);
    }

    /**
     * Creates a driver with preset capabilities.
     * 
     * @param capabilities
     *            The capabilities.
     */
    public HtmlUnitDriverLocal(Capabilities capabilities) {
        super(capabilities);
    }

    @Override
    public IParameterDecorator getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(IParameterDecorator parameters) {
        this.parameters = parameters;
    }

    /**
     * Get browser name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set browser name.
     * 
     * @param name
     *            A name.
     */
    public void setName(String name) {
        this.name = name;
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

    @Override
    public WebClient getWebClient() {
        return super.getWebClient();
    }

    @Override
    protected WebClient modifyWebClient(WebClient client) {
        parameters = new ParameterDecoratorImpl();
        parameters.setDecorated(this);
        return client;
    }

    /**
     * Adds a header to the client.
     * 
     * @param name
     *            The header name.
     * @param value
     *            The header value.
     */
    public void setHeader(String name, String value) {
        getWebClient().addRequestHeader(name, value);
    }

    @Override
    public void initialize() {
        setFeatures();
        WebClient client = getWebClient();
        setBasic(client);
        setConnection(client);
        setProxy(client);
        setCredentials(client);
        setCache(client);
        setTime(client);
    }

    /**
     * Prepare features from attribute information.
     */
    protected void setFeatures() {
        IFeatureManager fm = SRServices.getFeatureManager();
        if (host == null) {
            fm.set(FEATURE_HOST, this);
        }
        if (port == null) {
            fm.set(FEATURE_PORT, this);
        }
        if (username == null) {
            fm.set(FEATURE_USERNAME, this);
        }
        if (password == null) {
            fm.set(FEATURE_PASSWORD, this);
        }
        fm.set(FEATURE_HTTPTIMEOUT, this);
        if (connection == null) {
            fm.set(FEATURE_CONNECTION, this);
        }
        if (cache == null) {
            fm.set(FEATURE_CACHE, this);
        }
        if (cached == null) {
            fm.set(FEATURE_CACHED, this);
        }
    }

    /**
     * Basic adjusts.
     * 
     * @param client
     *            A client.
     */
    protected void setBasic(WebClient client) {
        // ignore SSL.
        client.getOptions().setUseInsecureSSL(true);

        // synchronize Ajax calls
        client.setAjaxController(new NicelyResynchronizingAjaxController());

        // replace error handlers
        client.setCssErrorHandler(new OptimizedCssErrorHandler());
        client.setIncorrectnessListener(new OptimizedIncorrectnessListener());
    }

    /**
     * Set connection information.
     * 
     * @param client
     *            A client.
     */
    @SuppressWarnings("unchecked")
    protected void setConnection(WebClient client) {
        if (connection != null) {
            try {
                connectionType = (Class<? extends IWebConnection>) Class.forName(connection);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Connection class '" + connection + "' not found, or is not a subtype of " + WebConnection.class.getName() + ".", e);
            }
        }
        // change web connection
        if (connectionType != null) {
            WebConnection connectionInstance = null;
            try {
                Constructor<? extends WebConnection> constr = connectionType.getConstructor(WebClient.class);
                connectionInstance = constr.newInstance(client);
            } catch (Exception e) {
                try {
                    connectionInstance = connectionType.newInstance();
                } catch (Exception e1) {
                    throw new RuntimeException("Constructor with WebClient argument or empty not found for '" + connectionType + "'.", e);
                }
            }
            client.setWebConnection(connectionInstance);
        }
    }

    /**
     * Set proxy setup.
     * 
     * @param client
     *            A client.
     */
    protected void setProxy(WebClient client) {
        if (host != null && port != null) {
            ProxyConfig config = new ProxyConfig(host, port);
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Browser named '" + getName() + "' proxy '" + host + ":" + port + "'.");
            }
            client.getOptions().setProxyConfig(config);
        }
    }

    /**
     * Set user credentials.
     * 
     * @param client
     *            A client.
     */
    protected void setCredentials(WebClient client) {
        if (username != null && password != null) {
            DefaultCredentialsProvider provider = new DefaultCredentialsProvider();
            provider.addCredentials(username, password);
            client.setCredentialsProvider(provider);
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Browser named '" + getName() + "' credentials '" + username + "|" + password + "'.");
            }
        }
    }

    /**
     * Set cache handler.
     * 
     * @param client
     *            A client.
     */
    @SuppressWarnings("unchecked")
    protected void setCache(WebClient client) {
        if (cache != null) {
            try {
                Class<? extends Cache> cacheType = (Class<? extends Cache>) Class.forName(cache);
                cacheInstance = cacheType.newInstance();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Cache class '" + cache + "' not found, or is not a subtype of " + Cache.class.getName() + ".", e);
            } catch (Exception e) {
                throw new RuntimeException("Cache class '" + cache + "' instance could not be created.", e);
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
    }

    /**
     * Set timeout information.
     * 
     * @param client
     *            A client.
     */
    protected void setTime(WebClient client) {
        client.getOptions().setTimeout(httptimeout);
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Browser named '" + getName() + "' bound to '" + client + "'.");
        }
    }
}