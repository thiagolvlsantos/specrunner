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
package org.specrunner.sql.positive;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.DontEval;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.PluginGroupImpl;
import org.specrunner.plugins.type.Command;
import org.specrunner.sql.AbstractPluginDatabase;
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
    private Class<? extends ISchemaLoader> schemaLoader = SchemaLoaderXOM.class;

    /**
     * Feature for database implementation.
     */
    public static final String FEATURE_DATABASE = PluginDbms.class.getName() + ".database";
    /**
     * The database class.
     */
    private Class<? extends IDatabase> database = DatabaseDefault.class;

    /**
     * Feature for system datasource name.
     */
    public static final String FEATURE_SYSTEM = PluginDbms.class.getName() + ".system";
    /**
     * The system datasource.
     */
    private String system = "org.hsqldb.jdbcDriver|jdbc:hsqldb:mem:TEST_INIT|sa|";

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
            fm.add(PluginDatabase.FEATURE_NAME, "systemDatabase");
            fm.add(PluginDatabase.FEATURE_REUSE, true);
        }
        {
            // set datasources
            fm.add(AbstractPluginDatabase.FEATURE_DATASOURCE, "systemConnection");
            fm.add(AbstractPluginDatabase.FEATURE_DATABASE, "systemDatabase");
        }
        {
            // set release bases
            fm.add(PluginRelease.FEATURE_NAME, "systemDatabase");
        }
    }

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

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_SYSTEM, this);
        {
            // database schema
            add(new PluginSchemaLoader());
            add(new PluginSchema());
        }
        {
            // system base
            PluginConnection systemConnection = new PluginConnection();
            systemConnection.setConnection(system);
            systemConnection.setName("systemConnection");
            add(systemConnection);
            PluginDatabase systemDatabase = new PluginDatabase();
            systemDatabase.setName("systemDatabase");
            add(systemDatabase);
        }
        {
            // drop base
            PluginScripts drop = new PluginScripts();
            drop.setClasspathrelative(true);
            drop.setValue("/sgbd_drop.sql");
            drop.setFailsafe(true);
            drop.setName("systemConnection");
            add(drop);
        }
        {
            // create base
            PluginScripts create = new PluginScripts();
            create.setClasspathrelative(true);
            create.setValue("/sgbd_final.sql");
            create.setFailsafe(true);
            create.setName("systemConnection");
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