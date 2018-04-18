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
package org.specrunner.sql.negative;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.DontEval;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.PluginGroupImpl;
import org.specrunner.plugins.type.Command;
import org.specrunner.sql.AbstractPluginDatabase;
import org.specrunner.sql.IDataSourceProvider;
import org.specrunner.sql.PluginCompareBase;
import org.specrunner.sql.PluginConnection;
import org.specrunner.sql.PluginDatabase;
import org.specrunner.sql.PluginFilter;
import org.specrunner.sql.PluginRelease;
import org.specrunner.sql.PluginSchema;
import org.specrunner.sql.PluginSchemaLoader;
import org.specrunner.sql.PluginScripts;
import org.specrunner.sql.database.IDatabase;
import org.specrunner.sql.database.impl.DatabaseDefault;
import org.specrunner.sql.meta.ISchemaLoader;
import org.specrunner.sql.meta.impl.SchemaLoaderXOM;

/**
 * Database macro.
 * 
 * @author Thiago Santos
 */
public class PluginDbms extends PluginGroupImpl {

    /**
     * Feature for schema loader.
     */
    public static final String FEATURE_SCHEMA_LOADER = PluginDbms.class.getName() + ".schemaLoader";
    /**
     * The database schema loader class.
     */
    protected Class<? extends ISchemaLoader> schemaLoader = SchemaLoaderXOM.class;

    /**
     * Feature for database implementation.
     */
    public static final String FEATURE_DATABASE = PluginDbms.class.getName() + ".database";
    /**
     * The database class.
     */
    protected Class<? extends IDatabase> database = DatabaseDefault.class;

    /**
     * Feature for system datasource name.
     */
    public static final String FEATURE_SYSTEM = PluginDbms.class.getName() + ".system";
    /**
     * The system datasource.
     */
    protected String system = "org.hsqldb.jdbcDriver|jdbc:hsqldb:mem:TEST_INIT|sa|";
    /**
     * Feature for system datasource provider name.
     */
    public static final String FEATURE_SYSTEM_PROVIDER = PluginDbms.class.getName() + ".systemProvider";
    /**
     * The system datasource provider.
     */
    protected String systemProvider;

    /**
     * Feature for system datasource provider instance.
     */
    public static final String FEATURE_SYSTEM_PROVIDER_INSTANCE = PluginDbms.class.getName() + ".systemProviderInstance";
    /**
     * The system datasource provider instace.
     */
    protected IDataSourceProvider systemProviderInstance;

    /**
     * Feature for reference datasource name.
     */
    public static final String FEATURE_REFERENCE = PluginDbms.class.getName() + ".reference";
    /**
     * The guide datasource.
     */
    protected String reference = "org.hsqldb.jdbcDriver|jdbc:hsqldb:mem:TEST_FINAL|sa|";
    /**
     * Feature for reference datasource provider name.
     */
    public static final String FEATURE_REFERENCE_PROVIDER = PluginDbms.class.getName() + ".referenceProvider";
    /**
     * The reference datasource provider.
     */
    protected String referenceProvider;

    /**
     * Feature for reference datasource provider instance.
     */
    public static final String FEATURE_REFERENCE_PROVIDER_INSTANCE = PluginDbms.class.getName() + ".referenceProviderInstance";
    /**
     * The reference datasource provider instace.
     */
    protected IDataSourceProvider referenceProviderInstance;

    /**
     * Get database schema loader class. Default is 'SchemaLoaderXOM'.
     * 
     * @return The schema loader.
     */
    public Class<? extends ISchemaLoader> getSchemaLoader() {
        return schemaLoader;
    }

    /**
     * Set schema loader class.
     * 
     * @param schemaLoader
     *            A loader.
     */
    public void setSchemaLoader(Class<? extends ISchemaLoader> schemaLoader) {
        this.schemaLoader = schemaLoader;
    }

    /**
     * Get database implementation class. Default is 'DatabaseDefault'.
     * 
     * @return Get implementation class.
     */
    public Class<? extends IDatabase> getDatabase() {
        return database;
    }

    /**
     * Set implementation class.
     * 
     * @param database
     *            Implementation class.
     */
    public void setDatabase(Class<? extends IDatabase> database) {
        this.database = database;
    }

    /**
     * Get the system database name.
     * 
     * @return The system database name.
     */
    public String getSystem() {
        return system;
    }

    /**
     * Set the system database name.
     * 
     * @param system
     *            The database name.
     */
    @DontEval
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * System database provider name.
     * 
     * @return The database name.
     */
    public String getSystemProvider() {
        return systemProvider;
    }

    /**
     * Set system provider class name.
     * 
     * @param systemProvider
     *            A classname.
     */
    public void setSystemProvider(String systemProvider) {
        this.systemProvider = systemProvider;
    }

    /**
     * Get data source provider instance.
     * 
     * @return A data source provider instance.
     */
    public IDataSourceProvider getSystemProviderInstance() {
        return systemProviderInstance;
    }

    /**
     * Set the system data source provider instance.
     * 
     * @param systemProviderInstance
     *            A data source provider.
     */
    public void setSystemProviderInstance(IDataSourceProvider systemProviderInstance) {
        this.systemProviderInstance = systemProviderInstance;
    }

    /**
     * Get the reference database name.
     * 
     * @return The reference name.
     */
    public String getReference() {
        return reference;
    }

    /**
     * Set the reference database name.
     * 
     * @param reference
     *            The reference.
     */
    @DontEval
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Reference database provider name.
     * 
     * @return The database name.
     */
    public String getReferenceProvider() {
        return referenceProvider;
    }

    /**
     * Set reference provider class name.
     * 
     * @param referenceProvider
     *            A classname.
     */
    public void setReferenceProvider(String referenceProvider) {
        this.referenceProvider = referenceProvider;
    }

    /**
     * Get data source provider instance.
     * 
     * @return A data source provider instance.
     */
    public IDataSourceProvider getReferenceProviderInstance() {
        return referenceProviderInstance;
    }

    /**
     * Set the reference data source provider instance.
     * 
     * @param referenceProviderInstance
     *            A data source provider.
     */
    public void setReferenceProviderInstance(IDataSourceProvider referenceProviderInstance) {
        this.referenceProviderInstance = referenceProviderInstance;
    }

    public PluginDbms() {
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_SCHEMA_LOADER, this);
        fm.set(FEATURE_DATABASE, this);
        {
            // set loader.
            fm.add(PluginSchemaLoader.FEATURE_PROVIDER, schemaLoader.getName());
            fm.add(PluginSchemaLoader.FEATURE_REUSE, true);
        }
        {
            // set schema.
            fm.add(PluginSchema.FEATURE_SOURCE, "/sgbd.cfg.xml");
            fm.add(PluginSchema.FEATURE_REUSE, true);
        }
        // set connections
        {
            fm.add(PluginConnection.FEATURE_REUSE, true);
        }
        // set databases
        {
            fm.add(PluginDatabase.FEATURE_PROVIDER, database.getName());
            fm.add(PluginDatabase.FEATURE_NAME, "systemDatabase;referenceDatabase");
            fm.add(PluginDatabase.FEATURE_REUSE, true);
        }
        {
            // set datasources
            fm.add(AbstractPluginDatabase.FEATURE_DATASOURCE, "systemConnection;referenceConnection");
            fm.add(AbstractPluginDatabase.FEATURE_DATABASE, "systemDatabase;referenceDatabase");
        }
        {
            // set comparator bases
            fm.add(PluginCompareBase.FEATURE_SYSTEM, "systemConnection");
            fm.add(PluginCompareBase.FEATURE_REFERENCE, "referenceConnection");
        }
        {
            // set release bases
            fm.add(PluginRelease.FEATURE_NAME, "systemDatabase;referenceDatabase");
        }
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_SYSTEM, this);
        fm.set(FEATURE_SYSTEM_PROVIDER, this);
        fm.set(FEATURE_SYSTEM_PROVIDER_INSTANCE, this);
        fm.set(FEATURE_REFERENCE, this);
        fm.set(FEATURE_REFERENCE_PROVIDER, this);
        fm.set(FEATURE_REFERENCE_PROVIDER_INSTANCE, this);
        {
            // database schema
            add(new PluginSchemaLoader());
            add(new PluginSchema());
        }
        {
            // system base
            PluginConnection systemConnection = new PluginConnection();
            systemConnection.setConnection(system);
            systemConnection.setProvider(systemProvider);
            systemConnection.setProviderInstance(systemProviderInstance);
            systemConnection.setName("systemConnection");
            add(systemConnection);
            PluginDatabase systemDatabase = new PluginDatabase();
            systemDatabase.setName("systemDatabase");
            add(systemDatabase);
        }
        {
            // reference base
            PluginConnection referenceConnection = new PluginConnection();
            referenceConnection.setConnection(reference);
            referenceConnection.setProvider(referenceProvider);
            referenceConnection.setProviderInstance(referenceProviderInstance);
            referenceConnection.setName("referenceConnection");
            add(referenceConnection);
            PluginDatabase referenceDatabase = new PluginDatabase();
            referenceDatabase.setName("referenceDatabase");
            add(referenceDatabase);
        }
        {
            // drop both bases
            PluginScripts drop = new PluginScripts();
            drop.setClasspathrelative(true);
            drop.setValue("/sgbd_drop.sql");
            drop.setFailsafe(true);
            drop.setName("systemConnection;referenceConnection");
            add(drop);
        }
        {
            // create both bases
            PluginScripts create = new PluginScripts();
            create.setClasspathrelative(true);
            create.setValue("/sgbd_final.sql");
            create.setFailsafe(true);
            create.setName("systemConnection;referenceConnection");
            add(create);
        }
        {
            // apply filter on comparisons
            add(new PluginFilter());
        }
        // initialize after children add
        super.initialize(context);
    }
}
