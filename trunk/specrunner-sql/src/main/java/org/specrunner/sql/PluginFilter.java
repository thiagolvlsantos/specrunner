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
import org.specrunner.sql.report.IFilter;
import org.specrunner.util.UtilLog;

/**
 * Add a IFilter instance to the environment.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginFilter extends AbstractPluginValue {

    /**
     * Default filter provider name.
     */
    public static final String DEFAULT_FILTER_NAME = "filterName";

    /**
     * Provider name feature.
     */
    public static final String FEATURE_FILTER = PluginFilter.class.getName() + ".provider";
    /**
     * Filter provider class name.
     */
    private String provider;

    /**
     * Filter instance feature.
     */
    public static final String FEATURE_FILTER_INSTANCE = PluginFilter.class.getName() + ".providerInstance";
    /**
     * Filter provider instance.
     */
    private IFilter providerInstance;

    /**
     * Default filter setting for reuse.
     */
    public static final String FEATURE_REUSE = PluginFilter.class.getName() + ".reuse";
    /**
     * Set filter as reusable.
     */
    private Boolean reuse = false;

    /**
     * The provide class name. Something that implements the interface
     * <code>IFilter</code>.
     * 
     * @return The provider class name, fully qualified.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Sets the filter provider class name.
     * 
     * @param provider
     *            The class which implements <code>IFilter</code> provider name.
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * The instance of a data source.
     * 
     * @return Filter provider instance.
     */
    public IFilter getProviderInstance() {
        return providerInstance;
    }

    /**
     * The instance of data source.
     * 
     * @param providerInstance
     *            The instance.
     */
    public void setProviderInstance(IFilter providerInstance) {
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
            fm.set(FEATURE_FILTER, this);
        }
        fm.set(FEATURE_FILTER_INSTANCE, this);
        fm.set(FEATURE_REUSE, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        final String currentName = getName() != null ? getName() : DEFAULT_FILTER_NAME;
        IReuseManager rm = SRServices.get(IReuseManager.class);
        if (reuse) {
            IReusable<?> ir = rm.get(currentName);
            if (ir != null) {
                Map<String, Object> cfg = new HashMap<String, Object>();
                cfg.put("name", currentName);
                cfg.put("provider", provider);
                cfg.put("providerInstance", providerInstance);
                if (ir.canReuse(cfg)) {
                    ir.reset();
                    Object obj = ir.getObject();
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Reusing Filter " + obj);
                    }
                    context.saveGlobal(currentName, obj);
                    result.addResult(Success.INSTANCE, context.peek());
                    return ENext.DEEP;
                }
            }
        }
        if (providerInstance == null) {
            if (provider != null) {
                try {
                    providerInstance = (IFilter) Class.forName(provider).newInstance();
                } catch (Exception e) {
                    throw new PluginException("Invalid IFilter provider '" + provider + "'.", e);
                }
            }

        } else {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Using instance " + providerInstance + ".");
            }
        }
        if (reuse) {
            rm.put(currentName, new AbstractReusable<IFilter>(currentName, providerInstance) {
                @Override
                public void reset() {
                }

                @Override
                public void release() {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Provider " + providerInstance + " released.");
                    }
                }

                @Override
                public boolean canReuse(Map<String, Object> cfg) {
                    boolean sameName = currentName != null && provider.equals(cfg.get("name"));
                    boolean sameProvider = provider != null && provider.equals(cfg.get("provider"));
                    boolean sameInstance = providerInstance != null && providerInstance == cfg.get("providerInstance");
                    return sameName && (sameProvider || sameInstance);
                }
            });
        }
        context.saveGlobal(currentName, providerInstance);
        result.addResult(Success.INSTANCE, context.peek());
        return ENext.DEEP;
    }

    /**
     * Gets the filter saved in context.
     * 
     * @param context
     *            The context.
     * @param name
     *            The filter provider name.
     * @return The filter provider.
     * @throws PluginException
     *             On lookup errors.
     */
    public static IFilter getFilter(IContext context, String name) throws PluginException {
        if (name == null) {
            name = DEFAULT_FILTER_NAME;
        }
        IFilter provider = (IFilter) context.getByName(name);
        if (provider == null) {
            throw new PluginException("Instance of '" + IFilter.class.getName() + "' named '" + name + "' not found. Use " + PluginFilter.class.getName() + " first.");
        }
        return provider;
    }
}