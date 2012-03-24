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
package org.specrunner.sql;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.SpecRunnerServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.context.IContext;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReusableManager;
import org.specrunner.reuse.impl.AbstractReusable;
import org.specrunner.sql.impl.SimpleDataSource;
import org.specrunner.util.UtilLog;

/**
 * Plugin to set connection information. The connection information must be
 * based on a provider (DataSource- see <code>IDataSourceProvider</code>), or
 * direct information with driver/url/user/password.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginConnection extends AbstractPluginValue {

    /**
     * Default connection provider name.
     */
    public static final String CONNECTION_PROVIDER = "connectionProvider";

    /**
     * Connection driver.
     */
    public static final String FEATURE_DRIVER = PluginConnection.class.getName() + ".driver";
    /**
     * Driver class.
     */
    private String driver;
    /**
     * Connection url.
     */
    public static final String FEATURE_URL = PluginConnection.class.getName() + ".url";
    /**
     * Database connection.
     */
    private String url;
    /**
     * Connection user.
     */
    public static final String FEATURE_USER = PluginConnection.class.getName() + ".user";
    /**
     * Database user.
     */
    private String user;
    /**
     * Connection password.
     */
    public static final String FEATURE_PASSWORD = PluginConnection.class.getName() + ".password";
    /**
     * Database password.
     */
    private String password;

    /**
     * Provider name feature.
     */
    public static final String FEATURE_PROVIDER = PluginConnection.class.getName() + ".provider";
    /**
     * Connection provider class name.
     */
    private String provider;

    /**
     * Provider instance feature.
     */
    public static final String FEATURE_PROVIDER_INSTANCE = PluginConnection.class.getName() + ".providerInstance";
    /**
     * Datasource provider instance.
     */
    private IDataSourceProvider providerInstance;

    /**
     * Default connection setting for reuse.
     */
    public static final String FEATURE_REUSE = PluginConnection.class.getName() + ".reuse";
    /**
     * Set connection as reusable.
     */
    private Boolean reuse = false;

    /**
     * When threadsafe='true' <code>SpecRunner</code> uses
     * <code>IConcurrentMapping</code> to resolve database url reference.
     */
    public static final String FEATURE_THREADSAFE = PluginConnection.class.getName() + ".threadsafe";
    /**
     * Enable use of <code>IConcurrentMapping</code> to perform thread specific
     * settings.
     */
    private Boolean threadsafe = false;

    /**
     * Driver information.
     * 
     * @return The driver name.
     */
    public String getDriver() {
        return driver;
    }

    /**
     * Sets the drive.
     * 
     * @param driver
     *            The driver.
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * The database URL connection.
     * 
     * @return The URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL.
     * 
     * @param url
     *            Database URL.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * The database user.
     * 
     * @return The user.
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets database user.
     * 
     * @param user
     *            The user.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * The database password.
     * 
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the database password.
     * 
     * @param password
     *            The database password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * The thread mode.
     * 
     * @return true, for thread safe connections.
     */
    public Boolean getThreadsafe() {
        return threadsafe;
    }

    /**
     * Set thread safe value.
     * 
     * @param threadsafe
     *            Thread safe state.
     */
    public void setThreadsafe(Boolean threadsafe) {
        this.threadsafe = threadsafe;
    }

    /**
     * The provide class name. Something that implements the interface
     * <code>IDataSourceProvider</code>.
     * 
     * @return The provider class name, fully qualified.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Sets the datasource provider class name.
     * 
     * @param provider
     *            The class which implements <code>IDataSourceProvider</code>
     *            provider name.
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * The instance of a data source.
     * 
     * @return Data source provider.
     */
    public IDataSourceProvider getProviderInstance() {
        return providerInstance;
    }

    /**
     * The instance of data source.
     * 
     * @param providerInstance
     *            The instance.
     */
    public void setProviderInstance(IDataSourceProvider providerInstance) {
        this.providerInstance = providerInstance;
    }

    /**
     * Gets the reuse status. If reuse is true, the object can be reused by
     * multiple tests.
     * 
     * @return The reuse status.
     */
    public Boolean getReuse() {
        return reuse;
    }

    /**
     * Set reusable state.
     * 
     * @param reuse
     *            true, for reuse, false, otherwise.
     */
    public void setReuse(Boolean reuse) {
        this.reuse = reuse;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        if (driver == null) {
            try {
                fh.set(FEATURE_DRIVER, "driver", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (url == null) {
            try {
                fh.set(FEATURE_URL, "url", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        if (user == null) {
            try {
                fh.set(FEATURE_USER, "user", String.class, this);
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
        if (provider == null) {
            try {
                fh.set(FEATURE_PROVIDER, "provider", String.class, this);
            } catch (FeatureManagerException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        try {
            fh.set(FEATURE_PROVIDER_INSTANCE, "providerInstance", IDataSourceProvider.class, this);
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
        try {
            fh.set(FEATURE_THREADSAFE, "threadsafe", Boolean.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        final String currentName = getName() != null ? getName() : CONNECTION_PROVIDER;
        IReusableManager rm = SpecRunnerServices.get(IReusableManager.class);
        if (reuse) {
            IReusable ir = rm.get(currentName);
            if (ir != null) {
                Map<String, Object> cfg = new HashMap<String, Object>();
                cfg.put("name", currentName);
                cfg.put("provider", provider);
                cfg.put("providerInstance", providerInstance);
                cfg.put("driver", driver);
                cfg.put("url", url);
                cfg.put("user", user);
                cfg.put("password", password);
                if (ir.canReuse(cfg)) {
                    ir.reset();
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Reusing DataSource " + ir.getObject());
                    }
                    context.saveGlobal(currentName, ir.getObject());
                    result.addResult(Status.SUCCESS, context.peek());
                    return ENext.DEEP;
                }
            }
        }
        if (providerInstance == null) {
            if (provider != null) {
                try {
                    providerInstance = (IDataSourceProvider) Class.forName(provider).newInstance();
                } catch (Exception e) {
                    throw new PluginException("Invalid DataSource provider '" + provider + "'.", e);
                }
            } else {
                if (driver != null && url != null && user != null && password != null) {
                    providerInstance = createProvider();
                } else {
                    throw new PluginException(PluginConnection.class.getSimpleName() + " must have a provider instance set using feature 'FEATURE_PROVIDER_INSTANCE', a generator of DataSource using feature 'FEATURE_PROVIDER', or connection informations 'driver/url/user/password' passed as attributes or their specific 'FEATURE_...'.");
                }
            }
        } else {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Using instance " + providerInstance + ".");
            }
        }
        if (reuse) {
            rm.put(currentName, new AbstractReusable(currentName, providerInstance) {
                @Override
                public void reset() {
                }

                @Override
                public void release() {
                    providerInstance.release();
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Provider " + providerInstance + " released.");
                    }
                }

                @Override
                public boolean canReuse(Map<String, Object> cfg) {
                    boolean sameName = currentName.equalsIgnoreCase((String) cfg.get("name"));
                    boolean sameProvider = provider != null && provider.equals(cfg.get("provider"));
                    boolean sameInstance = providerInstance != null && providerInstance == cfg.get("providerInstance");
                    boolean sameProperties = true;
                    sameProperties = driver != null && driver.equalsIgnoreCase((String) cfg.get("driver"));
                    sameProperties = sameProperties && url != null && url.equalsIgnoreCase((String) cfg.get("url"));
                    sameProperties = sameProperties && user != null && user.equalsIgnoreCase((String) cfg.get("user"));
                    sameProperties = sameProperties && password != null && password.equalsIgnoreCase((String) cfg.get("password"));
                    return sameName && (sameInstance || sameProvider || sameProperties);
                }
            });
        }
        context.saveGlobal(currentName, providerInstance);
        result.addResult(Status.SUCCESS, context.peek());
        return ENext.DEEP;
    }

    /**
     * Gets the datasource provider saved in context.
     * 
     * @param context
     *            The context.
     * @param name
     *            The datasource provider name.
     * @return The datasource provider.
     * @throws PluginException
     *             On lookup errors.
     */
    public static IDataSourceProvider getProvider(IContext context, String name) throws PluginException {
        if (name == null) {
            name = CONNECTION_PROVIDER;
        }
        IDataSourceProvider provider = (IDataSourceProvider) context.getByName(name);
        if (provider == null) {
            throw new PluginException("Instance of '" + IDataSourceProvider.class.getName() + "' not found. Use " + PluginConnection.class.getName() + " first.");
        }
        return provider;
    }

    /**
     * Creates a given provider.
     * 
     * @return The datasource provider.
     */
    public IDataSourceProvider createProvider() {
        String newUrl = url;
        if (threadsafe) {
            IConcurrentMapping rm = SpecRunnerServices.get(IConcurrentMapping.class);
            newUrl = String.valueOf(rm.get("url", newUrl));
        }
        return new SimpleDataSource(driver, newUrl, user, password);
    }
}
