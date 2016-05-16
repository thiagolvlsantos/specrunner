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
package org.specrunner.sql;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginValue;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.reuse.core.AbstractReusable;
import org.specrunner.sql.database.IDatabase;
import org.specrunner.sql.util.StringUtil;
import org.specrunner.util.UtilLog;

/**
 * Add an IDatabase to the environment.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginDatabase extends AbstractPluginValue {

    /**
     * Default database provider name.
     */
    public static final String DEFAULT_DATABASE_NAME = "databaseName";

    /**
     * Provider name feature.
     */
    public static final String FEATURE_PROVIDER = PluginDatabase.class.getName() + ".provider";
    /**
     * Connection provider class name.
     */
    private String provider;

    /**
     * Provider instance feature.
     */
    public static final String FEATURE_PROVIDER_INSTANCE = PluginDatabase.class.getName() + ".providerInstance";
    /**
     * Database provider instance.
     */
    private IDatabase[] providerInstance;

    /**
     * Default connection setting for reuse.
     */
    public static final String FEATURE_REUSE = PluginDatabase.class.getName() + ".reuse";
    /**
     * Set connection as reusable.
     */
    private Boolean reuse = false;

    /**
     * Database name feature.
     */
    public static final String FEATURE_NAME = PluginDatabase.class.getName() + ".name";

    /**
     * Feature for names separators.
     */
    public static final String FEATURE_SEPARATOR = PluginDatabase.class.getName() + ".separator";
    /**
     * Default separator.
     */
    public static final String DEFAULT_SEPARATOR = ";";
    /**
     * The separator, default is ";".
     */
    private String separator = DEFAULT_SEPARATOR;

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
    public IDatabase[] getProviderInstance() {
        return providerInstance;
    }

    /**
     * The instance of data source.
     * 
     * @param providerInstance
     *            The instance.
     */
    public void setProviderInstance(IDatabase[] providerInstance) {
        this.providerInstance = providerInstance == null ? null : Arrays.copyOf(providerInstance, providerInstance.length);
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

    /**
     * Get the name separator.
     * 
     * @return The separator.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Set the name separator.
     * 
     * @param separator
     *            The separator.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SRServices.getFeatureManager();
        if (provider == null) {
            fm.set(FEATURE_PROVIDER, this);
        }
        fm.set(FEATURE_PROVIDER_INSTANCE, this);
        fm.set(FEATURE_REUSE, this);
        if (getName() == null) {
            fm.set(FEATURE_NAME, this);
        }
        fm.set(FEATURE_SEPARATOR, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        String[] bases = StringUtil.tokenize(getName() != null ? getName() : DEFAULT_DATABASE_NAME, separator);
        if (providerInstance == null) {
            providerInstance = new IDatabase[bases.length];
        }
        int failure = 0;
        int index = 0;
        for (String base : bases) {
            final String currentName = base;
            IReuseManager rm = SRServices.get(IReuseManager.class);
            if (reuse) {
                final IReusable<?> ir = rm.get(currentName);
                if (ir != null) {
                    Map<String, Object> cfg = new HashMap<String, Object>();
                    cfg.put("provider", provider);
                    cfg.put("providerInstance" + index, providerInstance[index]);
                    if (ir.canReuse(cfg)) {
                        ir.reset();
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("Reusing IDatabase(" + currentName + ") " + ir.getObject());
                        }
                        context.saveGlobal(currentName, ir.getObject());
                        result.addResult(Success.INSTANCE, context.peek());
                        return ENext.DEEP;
                    }
                }
            }
            if (providerInstance[index] == null) {
                if (provider != null) {
                    try {
                        providerInstance[index] = (IDatabase) Class.forName(provider).newInstance();
                    } catch (Exception e) {
                        failure++;
                        result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Invalid Database provider(" + index + ") '" + provider + "' for " + currentName + ". Error:" + e.getMessage(), e));
                    }
                }
            } else {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Using instance " + providerInstance[index] + ".");
                }
            }
            if (providerInstance[index] != null) {
                providerInstance[index].initialize();
                providerInstance[index].setName(currentName);
            }
            if (reuse) {
                final int indexInner = index;
                rm.put(currentName, new AbstractReusable<IDatabase>(currentName, providerInstance[index]) {
                    @Override
                    public void reset() {
                        providerInstance[indexInner].initialize();
                    }

                    @Override
                    public void release() {
                        try {
                            providerInstance[indexInner].release();
                            if (UtilLog.LOG.isInfoEnabled()) {
                                UtilLog.LOG.info("Provider " + providerInstance[indexInner] + " released.");
                            }
                        } catch (PluginException e) {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug(e.getMessage(), e);
                            }
                        }
                    }

                    @Override
                    public boolean canReuse(Map<String, Object> cfg) {
                        boolean sameProvider = provider != null && provider.equals(cfg.get("provider"));
                        boolean sameInstance = providerInstance[indexInner] == cfg.get("providerInstance" + indexInner);
                        return sameProvider || sameInstance;
                    }
                });
            }
            context.saveGlobal(currentName, providerInstance[index]);
            index++;
        }
        if (failure == 0) {
            result.addResult(Success.INSTANCE, context.peek());
        }
        return ENext.DEEP;
    }

    /**
     * Gets the database provider saved in context.
     * 
     * @param context
     *            The context.
     * @param name
     *            The database provider name.
     * @return The database provider.
     * @throws PluginException
     *             On lookup errors.
     */
    public static synchronized IDatabase getDatabase(IContext context, String name) throws PluginException {
        if (name == null) {
            name = DEFAULT_DATABASE_NAME;
        }
        IDatabase provider = (IDatabase) context.getByName(name);
        if (provider == null) {
            throw new PluginException("Instance of '" + IDatabase.class.getName() + "' named '" + name + "' not found. Use " + PluginDatabase.class.getName() + " first.");
        }
        return provider;
    }
}
