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
package org.specrunner.sql;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.reuse.IReusable;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.reuse.impl.AbstractReusable;
import org.specrunner.sql.meta.ISchemaLoader;
import org.specrunner.sql.meta.Schema;
import org.specrunner.util.UtilLog;

/**
 * Uses the ISchemaLoader from environment to load a Schema and save it to the
 * environment.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginSchema extends AbstractPluginValue {

    /**
     * Default schema name.
     */
    public static final String DEFAULT_SCHEMA_NAME = "schemaName";

    /**
     * Provider instance feature.
     */
    public static final String FEATURE_PROVIDER_INSTANCE = PluginSchema.class.getName() + ".providerInstance";
    /**
     * Database provider instance.
     */
    private Schema providerInstance;

    /**
     * Schema source feature.
     */
    public static final String FEATURE_SOURCE = PluginSchema.class.getName() + ".source";

    /**
     * The objeto to read a schema from.
     */
    private Object source;

    /**
     * Default connection setting for reuse.
     */
    public static final String FEATURE_REUSE = PluginSchemaLoader.class.getName() + ".reuse";
    /**
     * Set connection as reusable.
     */
    private Boolean reuse = false;

    /**
     * The schema source.
     * 
     * @return The source.
     */
    public Object getSource() {
        return source;
    }

    /**
     * Set the schema source.
     * 
     * @param source
     *            A new source.
     */
    public void setSource(Object source) {
        this.source = source;
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
        if (providerInstance == null) {
            fm.set(FEATURE_PROVIDER_INSTANCE, this);
        }
        fm.set(FEATURE_SOURCE, this);
        fm.set(FEATURE_REUSE, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        ISchemaLoader provider = PluginSchemaLoader.getLoader(context, getName());
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("PluginSchema loader:" + provider);
        }
        final String currentName = getName() != null ? getName() : DEFAULT_SCHEMA_NAME;
        IReuseManager rm = SRServices.get(IReuseManager.class);
        if (reuse) {
            IReusable<?> ir = rm.get(currentName);
            if (ir != null) {
                Map<String, Object> cfg = new HashMap<String, Object>();
                cfg.put("source", source);
                cfg.put("providerInstance", providerInstance);
                if (ir.canReuse(cfg)) {
                    ir.reset();
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Reusing Schema " + ir.getObject());
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
                    providerInstance = provider.load(source);
                } catch (Exception e) {
                    throw new PluginException("Invalid ISchemaLoader provider '" + providerInstance + "'.", e);
                }
            }

        } else {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Using instance " + providerInstance + ".");
            }
        }
        if (reuse) {
            rm.put(currentName, new AbstractReusable<Schema>(currentName, providerInstance) {
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
                    boolean sameSource = source != null && source.equals(cfg.get("source"));
                    boolean sameInstance = providerInstance != null && providerInstance == cfg.get("providerInstance");
                    return sameSource || sameInstance;
                }
            });
        }
        context.saveGlobal(currentName, providerInstance);
        result.addResult(Success.INSTANCE, context.peek());
        return ENext.DEEP;
    }

    /**
     * Gets the schema set in environment with the given name.
     * 
     * @param context
     *            The text context.
     * @param name
     *            The database name.
     * @return The schema.
     * @throws PluginException
     *             On lookup errors.
     */
    public static synchronized Schema getSchema(IContext context, String name) throws PluginException {
        if (name == null) {
            name = DEFAULT_SCHEMA_NAME;
        }
        Schema provider = (Schema) context.getByName(name);
        if (provider == null) {
            throw new PluginException("Instance of '" + Schema.class.getName() + "' named '" + name + "' not found. Use " + PluginSchema.class.getName() + " first.");
        }
        return provider;
    }
}