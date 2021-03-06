/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.specrunner.SRServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginValue;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.reuse.core.AbstractReusable;
import org.specrunner.sql.impl.SimpleDataSource;
import org.specrunner.util.UtilLog;

/**
 * Plugin to set connection information. The connection information must be
 * based on a provider (DataSource- see <code>IDataSourceProvider</code>), or
 * direct information with driver/url/user/password.
 * <p>
 * When threadsafe='true' <code>SpecRunner</code> uses
 * <code>IConcurrentMapping</code> to resolve database url reference.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginConnection extends AbstractPluginValue {

    /**
     * Default connection name.
     */
    public static final String DEFAULT_CONNECTION_NAME = "connectionName";

    /**
     * Full connection descriptor.
     */
    public static final String FEATURE_CONNECTION = PluginConnection.class.getName() + ".connection";
    /**
     * Connection descriptor.
     */
    private String connection;
    /**
     * Connection separator string.
     */
    public static final String FEATURE_SEPARATOR = PluginConnection.class.getName() + ".separator";
    /**
     * Default string separator.
     */
    public static final String DEFAULT_SEPARATOR = "|";
    /**
     * Connection separator.
     */
    private String separator = DEFAULT_SEPARATOR;
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
     * Connection information.
     * 
     * @return The connection description as
     *         '&lt;driver&gt;|&lt;url&gt;|&lt;user&gt;|&lt;password&gt;'.
     */
    public String getConnection() {
        return connection;
    }

    /**
     * Sets the connection description as
     * '&lt;driver&gt;|&lt;url&gt;|&lt;user&gt;|&lt;password&gt;'.
     * 
     * @param connection
     *            The connection.
     */
    public void setConnection(String connection) {
        this.connection = connection;
        if (connection == null) {
            return;
        }
        List<Integer> indexes = new LinkedList<Integer>();
        String other = separator + connection + separator;
        for (int i = 0; i < other.length(); i++) {
            if (other.charAt(i) == separator.charAt(0)) {
                indexes.add(i);
            }
        }
        for (int i = 0; i < indexes.size() - 1; i++) {
            String tmp = connection.substring(indexes.get(i), indexes.get(i + 1) - 1);
            if (driver == null) {
                setDriver(tmp);
                continue;
            }
            if (url == null) {
                setUrl(tmp);
                continue;
            }
            if (user == null) {
                setUser(tmp);
                continue;
            }
            if (password == null) {
                setPassword(tmp);
                continue;
            }
        }
    }

    /**
     * Gets the current separator.
     * 
     * @return The separator.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Set the connection separator.
     * 
     * @param separator
     *            The separator.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

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
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        if (connection == null) {
            fm.set(FEATURE_CONNECTION, this);
        }
        if (driver == null) {
            fm.set(FEATURE_DRIVER, this);
        }
        if (url == null) {
            fm.set(FEATURE_URL, this);
        }
        if (user == null) {
            fm.set(FEATURE_USER, this);
        }
        if (password == null) {
            fm.set(FEATURE_PASSWORD, this);
        }
        if (provider == null) {
            fm.set(FEATURE_PROVIDER, this);
        }
        fm.set(FEATURE_PROVIDER_INSTANCE, this);
        fm.set(FEATURE_REUSE, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        final String currentName = getName() != null ? getName() : DEFAULT_CONNECTION_NAME;
        IReuseManager rm = SRServices.get(IReuseManager.class);
        if (reuse) {
            IReusable<?> ir = rm.get(currentName);
            if (ir != null) {
                Map<String, Object> cfg = new HashMap<String, Object>();
                cfg.put("name", currentName);
                cfg.put("connection", connection);
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
                    result.addResult(Success.INSTANCE, context.peek());
                    return ENext.DEEP;
                }
            }
        }
        if (providerInstance == null) {
            if (provider != null) {
                try {
                    providerInstance = (IDataSourceProvider) Class.forName(provider).newInstance();
                } catch (Exception e) {
                    throw new PluginException("Invalid IDataSourceProvider '" + provider + "'.", e);
                }
            } else {
                if (driver != null && url != null && user != null && password != null) {
                    providerInstance = createProvider();
                } else {
                    throw new PluginException(getClass().getSimpleName() + " must have a provider instance set using feature 'FEATURE_FILTER_INSTANCE', a generator of DataSource using feature 'FEATURE_FILTER', or connection information 'driver/url/user/password' passed as attributes or their specific 'FEATURE_...XXX' set.");
                }
            }
        } else {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Using instance " + providerInstance + ".");
            }
        }
        if (reuse) {
            rm.put(currentName, new AbstractReusable<IDataSourceProvider>(currentName, providerInstance) {
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
                    boolean sameConnection = connection != null && connection.equals(cfg.get("connection"));
                    boolean sameProvider = provider != null && provider.equals(cfg.get("provider"));
                    boolean sameInstance = providerInstance != null && providerInstance == cfg.get("providerInstance");
                    boolean sameProperties = true;
                    sameProperties = driver != null && driver.equalsIgnoreCase((String) cfg.get("driver"));
                    sameProperties = sameProperties && url != null && url.equalsIgnoreCase((String) cfg.get("url"));
                    sameProperties = sameProperties && user != null && user.equalsIgnoreCase((String) cfg.get("user"));
                    sameProperties = sameProperties && password != null && password.equalsIgnoreCase((String) cfg.get("password"));
                    return sameName && (sameConnection || sameInstance || sameProvider || sameProperties);
                }
            });
        }
        context.saveGlobal(currentName, providerInstance);
        result.addResult(Success.INSTANCE, context.peek());
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
    public static synchronized IDataSourceProvider getProvider(IContext context, String name) throws PluginException {
        if (name == null) {
            name = DEFAULT_CONNECTION_NAME;
        }
        IDataSourceProvider provider = (IDataSourceProvider) context.getByName(name);
        if (provider == null) {
            throw new PluginException("Instance of '" + IDataSourceProvider.class.getName() + "' named '" + name + "' not found. Use " + PluginConnection.class.getName() + " first.");
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
        if (getThreadsafe()) {
            IConcurrentMapping rm = SRServices.get(IConcurrentMapping.class);
            newUrl = String.valueOf(rm.get("url", newUrl));
        }
        return new SimpleDataSource(driver, newUrl, user, password);
    }
}
