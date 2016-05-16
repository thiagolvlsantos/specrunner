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
import org.specrunner.result.status.Success;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.reuse.core.AbstractReusable;
import org.specrunner.sql.meta.ISchemaLoader;
import org.specrunner.util.UtilLog;

/**
 * Add a ISchemaLoader to the environment.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginSchemaLoader extends AbstractPluginValue {

    /**
     * Default database provider name.
     */
    public static final String DEFAULT_SCHEMALOADER_NAME = "schemaLoaderName";

    /**
     * Provider name feature.
     */
    public static final String FEATURE_PROVIDER = PluginSchemaLoader.class.getName() + ".provider";
    /**
     * Connection provider class name.
     */
    private String provider;

    /**
     * Provider instance feature.
     */
    public static final String FEATURE_PROVIDER_INSTANCE = PluginSchemaLoader.class.getName() + ".providerInstance";
    /**
     * Database provider instance.
     */
    private ISchemaLoader providerInstance;

    /**
     * Default connection setting for reuse.
     */
    public static final String FEATURE_REUSE = PluginSchemaLoader.class.getName() + ".reuse";
    /**
     * Set connection as reusable.
     */
    private Boolean reuse = false;

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
    public ISchemaLoader getProviderInstance() {
        return providerInstance;
    }

    /**
     * The instance of data source.
     * 
     * @param providerInstance
     *            The instance.
     */
    public void setProviderInstance(ISchemaLoader providerInstance) {
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
        if (provider == null) {
            fm.set(FEATURE_PROVIDER, this);
        }
        fm.set(FEATURE_PROVIDER_INSTANCE, this);
        fm.set(FEATURE_REUSE, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        final String currentName = getName() != null ? getName() : DEFAULT_SCHEMALOADER_NAME;
        IReuseManager rm = SRServices.get(IReuseManager.class);
        if (reuse) {
            IReusable<?> ir = rm.get(currentName);
            if (ir != null) {
                Map<String, Object> cfg = new HashMap<String, Object>();
                cfg.put("provider", provider);
                cfg.put("providerInstance", providerInstance);
                if (ir.canReuse(cfg)) {
                    ir.reset();
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Reusing ISchemaLoader " + ir.getObject());
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
                    providerInstance = (ISchemaLoader) Class.forName(provider).newInstance();
                } catch (Exception e) {
                    throw new PluginException("Invalid ISchemaLoader provider '" + provider + "'.", e);
                }
            }

        } else {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Using instance " + providerInstance + ".");
            }
        }
        if (reuse) {
            rm.put(currentName, new AbstractReusable<ISchemaLoader>(currentName, providerInstance) {
                @Override
                public void reset() {
                }

                @Override
                public void release() {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug("Provider " + providerInstance + " released.");
                    }
                }

                @Override
                public boolean canReuse(Map<String, Object> cfg) {
                    boolean sameProvider = provider != null && provider.equals(cfg.get("provider"));
                    boolean sameInstance = providerInstance != null && providerInstance == cfg.get("providerInstance");
                    return sameProvider || sameInstance;
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
    public static synchronized ISchemaLoader getLoader(IContext context, String name) throws PluginException {
        if (name == null) {
            name = DEFAULT_SCHEMALOADER_NAME;
        }
        ISchemaLoader provider = (ISchemaLoader) context.getByName(name);
        if (provider == null) {
            throw new PluginException("Instance of '" + ISchemaLoader.class.getName() + "' named '" + name + "' not found. Use " + PluginSchemaLoader.class.getName() + " first.");
        }
        return provider;
    }
}
